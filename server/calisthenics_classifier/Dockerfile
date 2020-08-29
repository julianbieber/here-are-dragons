FROM tensorflow/tensorflow:latest-devel-py3

ADD logging.conf /opt/

ADD requirements.txt /opt/
RUN pip3 install -r /opt/requirements.txt

ADD models /models

ADD gunincorn.sh /opt/
ADD train.py /opt/
ADD api.py /opt/

EXPOSE 5000

RUN pip freeze

WORKDIR /opt
ENTRYPOINT ["/opt/gunincorn.sh", "headless"]