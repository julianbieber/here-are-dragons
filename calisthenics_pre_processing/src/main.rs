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
    #[structopt(long)]
    pub without_input_gyro: bool,
    #[structopt(long)]
    pub without_input_acceleration: bool,
    #[structopt(long)]
    pub without_acceleration: bool,
}

type ClassifierRow = (
    String,
    f32,
    f32,
    f32,
    f32,
    f32,
    f32,
    f32,
    f32,
    f32,
    f32,
    f32,
);

fn main() -> Result<(), Box<dyn Error>> {
    let opt: Opt = Opt::from_args();

    let mut csv_writer = csv::Writer::from_path(opt.dst)?;

    if opt.src.is_dir() {
        for f in opt.src.read_dir()? {
            let mut total_rows: Vec<ClassifierRow> = Vec::new();
            let file = f?;
            let mut rdr = csv::Reader::from_reader(BufReader::new(File::open(file.path())?));
            for result in rdr.records() {
                let row = result?;
                total_rows.push((
                    row.get(0).expect("").to_string(),
                    row.get(1).expect("").parse::<f32>()?,
                    row.get(2).expect("").parse::<f32>()?,
                    row.get(3).expect("").parse::<f32>()?,
                    row.get(4).expect("").parse::<f32>()?,
                    row.get(5).expect("").parse::<f32>()?,
                    row.get(6).expect("").parse::<f32>()?,
                    row.get(7).expect("").parse::<f32>()?,
                    row.get(8).expect("").parse::<f32>()?,
                    row.get(9).expect("").parse::<f32>()?,
                    row.get(10).expect("").parse::<f32>()?,
                    row.get(11).expect("").parse::<f32>()?,
                ))
            }
            let activities = split(total_rows, opt.cutoff_distance);

            for activity in activities {
                let remaining = drop_last_seconds(activity, opt.cutoff);
                for chunk in remaining.chunks_exact(30) {
                    let name = chunk.first().expect("").0.clone();
                    let vector: Vec<f32> = if opt.without_input_gyro {
                        chunk.iter().flat_map(|v| vec![v.8, v.9, v.10]).collect()
                    } else if opt.without_input_acceleration {
                        chunk
                            .iter()
                            .flat_map(|v| vec![v.1, v.2, v.3, v.4, v.5, v.6, v.7])
                            .collect()
                    } else if opt.without_acceleration {
                        chunk
                            .iter()
                            .flat_map(|v| vec![v.4, v.5, v.6, v.7])
                            .collect()
                    } else {
                        chunk
                            .iter()
                            .flat_map(|v| vec![v.1, v.2, v.3, v.4, v.5, v.6, v.7, v.8, v.9, v.10])
                            .collect()
                    };
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
        }
    }

    csv_writer.flush()?;

    Ok(())
}

fn split(rows: Vec<ClassifierRow>, cutoff_distance: f32) -> Vec<Vec<ClassifierRow>> {
    let mut activities = Vec::new();
    let mut activity: Vec<ClassifierRow> = Vec::new();
    for (name, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, time) in rows {
        if activity.is_empty() {
            activity.push((name, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, time));
        } else {
            let last = activity.last().expect("");
            if time - last.11 < cutoff_distance && name == last.0 && time > last.11 {
                activity.push((name, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, time));
            } else {
                activities.push(activity);
                activity = Vec::new();
                activity.push((name, x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, time));
            }
        }
    }
    activities.push(activity);

    activities
}

fn drop_last_seconds(rows: Vec<ClassifierRow>, cutoff: f32) -> Vec<ClassifierRow> {
    let last = rows.last().expect("").11;
    rows.into_iter()
        .rev()
        .filter(|v| last - v.11 > cutoff)
        .rev()
        .collect()
}
