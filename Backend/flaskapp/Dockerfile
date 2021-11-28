FROM ubuntu:20.04

ENV GROUP_ID=1000 \
    USER_ID=1000

RUN apt update && apt install -y \
    build-essential libssl-dev libffi-dev python3-dev python3-pip mongodb-clients

ADD . /var/www/flaskapp

WORKDIR /var/www/flaskapp

RUN pip install -r requirements.txt
RUN pip install gunicorn

RUN addgroup --gid $GROUP_ID www
RUN adduser --system --uid $USER_ID --gid $GROUP_ID --shell /bin/bash www

USER www

EXPOSE 5000

CMD [ "gunicorn", "-w", "4", "--bind", "0.0.0.0:5000", "wsgi"]