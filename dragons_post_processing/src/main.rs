use std::error::Error;
use std::fs::File;
use std::io::BufReader;
use std::path::PathBuf;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    #[structopt(long)]
    pub src: PathBuf,
    #[structopt(long)]
    pub dst: PathBuf,
    #[structopt(long)]
    pub cutoff: f32,
    #[structopt(long)]
    pub cutoff_distance: f32,
}

fn main() -> Result<(), Box<dyn Error>> {
    let opt: Opt = Opt::from_args();

    let mut total_rows: Vec<(String, f32, f32, f32, f32)> = Vec::new();

    if opt.src.is_dir() {
        for f in opt.src.read_dir()? {
            let file = f?;
            let mut rdr = csv::Reader::from_reader(BufReader::new(File::open(file.path())?));
            for result in rdr.records() {
                let row = result?;
                total_rows.push((
                    row.get(0).expect("").to_string(),
                    row.get(1 + 7).expect("").parse::<f32>()?,
                    row.get(2 + 7).expect("").parse::<f32>()?,
                    row.get(3 + 7).expect("").parse::<f32>()?,
                    row.get(4 + 7).expect("").parse::<f32>()?,
                ))
            }
        }
    }

    let activities = split(total_rows, opt.cutoff_distance);

    let mut csv_writer = csv::Writer::from_path(opt.dst)?;

    for activity in activities {
        let remaining = drop_last_seconds(activity, opt.cutoff);
        for chunk in remaining.chunks_exact(30) {
            let name = chunk.first().expect("").0.clone();
            let vector: Vec<f32> = chunk.iter().flat_map(|v| vec![v.1, v.2, v.3]).collect();
            if name == "push" {
                csv_writer.write_field("1")?;
                csv_writer.write_field("0")?;
                csv_writer.write_field("0")?;
            } else if name == "pull" {
                csv_writer.write_field("0")?;
                csv_writer.write_field("1")?;
                csv_writer.write_field("0")?;
            } else {
                csv_writer.write_field("0")?;
                csv_writer.write_field("0")?;
                csv_writer.write_field("1")?;
            }
            for v in vector {
                csv_writer.write_field(format!("{}", v).as_str())?;
            }
            csv_writer.write_record(None::<&[u8]>)?;
        }
    }
    csv_writer.flush()?;

    Ok(())
}

fn split(
    rows: Vec<(String, f32, f32, f32, f32)>,
    cutoff_distance: f32,
) -> Vec<Vec<(String, f32, f32, f32, f32)>> {
    let mut activities = Vec::new();
    let mut activity: Vec<(String, f32, f32, f32, f32)> = Vec::new();
    for (name, x, y, z, time) in rows {
        if activity.is_empty() {
            activity.push((name, x, y, z, time));
        } else {
            let last = activity.last().expect("");
            if time - last.4 < cutoff_distance && name == last.0 && time > last.4 {
                activity.push((name, x, y, z, time));
            } else {
                activities.push(activity);
                activity = Vec::new();
                activity.push((name, x, y, z, time));
            }
        }
    }
    activities.push(activity);

    activities
}

fn drop_last_seconds(
    rows: Vec<(String, f32, f32, f32, f32)>,
    cutoff: f32,
) -> Vec<(String, f32, f32, f32, f32)> {
    let last = rows.last().expect("").4;
    rows.into_iter()
        .rev()
        .filter(|v| last - v.4 > cutoff)
        .rev()
        .collect()
}
