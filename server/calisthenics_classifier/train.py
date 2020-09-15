import click
import numpy as np
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Conv2D, MaxPooling2D, Dense, Flatten, Reshape
from pandas import read_csv


class Model:

    def __init__(self, input_size):
        self.model_path = "models/activity_" + str(input_size)
        self.input_size = input_size
        self.model = Sequential([
            Reshape((10, input_size, 3), input_shape=(input_size * 30,)),
            Conv2D(16, (input_size, 3), input_shape=(10, input_size, 3)),
            MaxPooling2D(pool_size=1),
            Flatten(),
            Dense(16, activation='relu'),
            Dense(3, activation='softmax'),
        ])
        self.model.compile(
            'sgd',
            loss='categorical_crossentropy',
            metrics=['categorical_accuracy'],
        )

    def load(self, base):
        self.model.load_weights(base + self.model_path)

    def train(self, train_data, train_labels, test_data, test_labels):
        self.model.fit(
            train_data,
            train_labels,
            epochs=25,
            batch_size=1,
            validation_data=(test_data, test_labels),
        )
        self.model.save_weights(self.model_path)

    def predict(self, l):
        array = np.array(l, ndmin=1)
        array = array.reshape((1, self.input_size * 30))
        prediction = self.model.predict(array)
        return prediction[0][0].item(), prediction[0][1].item(), prediction[0][2].item()


def load_dataset(filename):
    # load the dataset as a pandas DataFrame
    data = read_csv(filename, header=None)
    # retrieve numpy array
    dataset = data.values
    # split into input (X) and output (y) variables
    X = dataset[:, 3:]
    y = dataset[:, :3]
    # format all fields as string
    # reshape target to be a 2d array
    return X, y


@click.command()
@click.option('--train_data')
@click.option('--test_data')
@click.option('--input_size', type=int)
def main(train_data, test_data, input_size):
    (dataset, labels) = load_dataset(train_data)
    (test_dataset, test_labels) = load_dataset(test_data)
    model = Model(input_size)
    model.train(dataset, labels, test_dataset, test_labels)


if __name__ == '__main__':
    main()

