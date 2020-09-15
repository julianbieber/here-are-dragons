#!/bin/bash

calisthenics_pre_processing --src ../data --dst ../all.csv --cutoff 1 --cutoff-distance 0.4
cat ../all.csv | shuf > ../shuffled.csv
head -n 83 ../shuffled.csv > ../train.csv
tail -n 36 ../shuffled.csv > ../test.csv

calisthenics_pre_processing --src ../only_accelerometer_data --dst ../input_acceleration.csv --cutoff 1 --cutoff-distance 0.4 --without-input-gyro
cat ../input_acceleration.csv | shuf > ../input_acceleration_shuffled.csv
head -n 308 ../input_acceleration_shuffled.csv > ../input_acceleration_train.csv
tail -n 133 ../input_acceleration_shuffled.csv > ../input_acceleration_test.csv

calisthenics_pre_processing --src ../data --dst ../gyro.csv --cutoff 1 --cutoff-distance 0.4 --without-input-acceleration
cat ../gyro.csv | shuf > ../gyro_shuffled.csv
head -n 83 ../gyro_shuffled.csv > ../gyro_train.csv
tail -n 36 ../gyro_shuffled.csv > ../gyro_test.csv

calisthenics_pre_processing --src ../data --dst ../gyro_attitude.csv --cutoff 1 --cutoff-distance 0.4 --without-acceleration
cat ../gyro_attitude.csv | shuf > ../gyro_attitude_shuffled.csv
head -n 83 ../gyro_attitude_shuffled.csv > ../gyro_attitude_train.csv
tail -n 36 ../gyro_attitude_shuffled.csv > ../gyro_attitude_test.csv

cd ../server/calisthenics_classifier/
python3 train.py --train_data ../../train.csv --test_data ../../test.csv --input_size 10
echo
python3 train.py --train_data ../../input_acceleration_train.csv --test_data ../../input_acceleration_test.csv --input_size 3
echo
python3 train.py --train_data ../../gyro_train.csv --test_data ../../gyro_test.csv --input_size 7
echo
python3 train.py --train_data ../../gyro_attitude_train.csv --test_data ../../gyro_attitude_test.csv --input_size 4

python3 confusion_matrix.py --test_data /home/jbieber/here-are-dragons/test.csv --input_size 10
python3 confusion_matrix.py --test_data /home/jbieber/here-are-dragons/input_acceleration_test.csv --input_size 3
python3 confusion_matrix.py --test_data /home/jbieber/here-are-dragons/gyro_test.csv --input_size 7
python3 confusion_matrix.py --test_data /home/jbieber/here-are-dragons/gyro_attitude_test.csv --input_size 4