from train import Model
import click


@click.command()
@click.option('--test_data')
@click.option('--input_size', type=int)
def main(test_data, input_size):
    model = Model(input_size)
    model.load("")
    matrix = {}
    activities = ["push", "pull", "other"]
    for a in activities:
        for b in activities:
            if matrix.get(a) is None:
                matrix[a] = {}
            matrix[a][b] = 0
    with open(test_data) as f:
        for line in f:
            line = line.split(",")
            activity_vector = line[:3]
            if activity_vector[0] == "1":
                activity = activities[0]
            elif activity_vector[1] == "1":
                activity = activities[1]
            else:
                activity = activities[2]
            v = list(map(lambda x: float(x), line[3:]))
            (x, y, z) = model.predict(v)

            if x > y and x > z:
                matrix[activity][activities[0]] += 1
            if y > x and y > z:
                matrix[activity][activities[1]] += 1
            if z > x and z > y:
                matrix[activity][activities[2]] += 1
    print("""
\\begin{table}[H]
    \centering
    \\begin{tabular}{ c c c c }""")
    print(" & " + " & ".join(activities) + "\\\\")
    for activity in activities:
        row = activity
        for activity_detected in activities:
            confusion = matrix[activity][activity_detected] / sum(matrix[activity].values())
            row += " & " + f"{confusion:.2f}"
        print(row + "\\\\")
    print("""
\\end{tabular}
    \\caption{Confusion Matrix ABC}
    \\label{tab:conf_ABC}
\\end{table}
""")



if __name__ == '__main__':
    main()


"""
\\begin{table}[]
    \centering
    \\begin{tabular}{ c c c c }
\\hline

\\end{tabular}
    \\caption{Confusion Matrix ABC}
    \\label{tab:conf_ABC}
\\end{table}
"""