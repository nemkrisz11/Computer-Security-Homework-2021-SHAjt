FROM ubuntu:20.04

ENV GROUP_ID=1000 \
    USER_ID=1000

RUN apt update && apt install -y \
    build-essential libssl-dev libffi-dev python3-dev python3-pip mongodb-clients

WORKDIR /tmp
ADD flaskapp/requirements.txt requirements.txt

RUN pip install -r requirements.txt
RUN pip install gunicorn

RUN addgroup --gid $GROUP_ID www
RUN adduser --system --uid $USER_ID --gid $GROUP_ID --shell /bin/bash www

ADD parser /var/www/parser
WORKDIR /var/www/parser
RUN python3 setup.py install

WORKDIR /var/www/flaskapp

RUN mkdir /var/www/uploads
RUN chown www /var/www/uploads

USER www

EXPOSE 5000

CMD [ "gunicorn", "-w", "4", "--bind", "0.0.0.0:5000", "wsgi"]