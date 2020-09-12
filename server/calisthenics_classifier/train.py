import click
import numpy as np
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Conv2D, MaxPooling2D, Dense, Flatten, Reshape
from pandas import read_csv


class Model:
    def __init__(self):

        self.model = Sequential([
            Reshape((30, 3, 1), input_shape=(90,)),
            Conv2D(8, 3, input_shape=(30, 3, 1)),
            MaxPooling2D(pool_size=1),
            Flatten(),
            Dense(8, activation='relu'),
            Dense(3, activation='softmax'),
        ])
        self.model.compile(
            'adam',
            loss='categorical_crossentropy',
            metrics=['accuracy'],
        )

    def load(self, file):
        self.model.load_weights(file)

    def train(self, train_data, train_labels, test_data, test_labels):
        self.model.fit(
            train_data,
            train_labels,
            epochs=10,
            batch_size=1,
            validation_data=(test_data, test_labels),
        )
        self.model.save_weights("models/activity")

    def predict(self, l):
        array = np.array(l, ndmin=1)
        array = array.reshape((1, 90))

        prediction = self.model.predict(array)
        return prediction[0][0].item(), prediction[0][1].item(), prediction[0][2].item()


def load_dataset(filename):
    # load the dataset as a pandas DataFrame
    data = read_csv(filename, header=None)
    # retrieve numpy array
    dataset = data.values
    # split into input (X) and output (y) variables
    X = dataset[:, 2:-1]
    y = dataset[:, :3]
    # format all fields as string
    # reshape target to be a 2d array
    print(y.shape)
    return X, y


@click.command()
@click.option('--train_data')
@click.option('--test_data')
def main(train_data, test_data):
    (dataset, labels) = load_dataset(train_data)
    (test_dataset, test_labels) = load_dataset(test_data)
    model = Model()
    model.train(dataset, labels, test_dataset, test_labels)


if __name__ == '__main__':
    main()

