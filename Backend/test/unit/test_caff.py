from datetime import datetime

import pytest

from fixtures import client, token
from io import BytesIO
from mongoengine import connect
from flaskapp.database.models import CaffFile
import os


@pytest.mark.username("testuser")
@pytest.mark.password("test1234")
def test_caff_upload(client, token):
    data = {'name': 'this is a name'}

    resp = client.post("/caff/upload", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 400 and resp.is_json and "file not in request" in resp.json["errorMessage"]

    data["file"] = (BytesIO(b"asdasdasdasdasdasdasdasdas"), "1.caff")
    resp = client.post("/caff/upload", headers={"Authorization": "Bearer " + token}, content_type='multipart/form-data',
                       data=data)
    assert resp.status_code == 400 and resp.is_json and "invalid file format" in resp.json["errorMessage"]

    data["file"] = (BytesIO(b"asdasdasdasdasdasdasdasdas"), "1")
    resp = client.post("/caff/upload", headers={"Authorization": "Bearer " + token}, content_type='multipart/form-data',
                       data=data)
    assert resp.status_code == 400 and resp.is_json and "invalid file format" in resp.json["errorMessage"]

    data["file"] = (BytesIO(b"12345678" * 1024 * 1024 * 60), "1")
    resp = client.post("/caff/upload", headers={"Authorization": "Bearer " + token}, content_type='multipart/form-data',
                       data=data)
    assert resp.status_code == 413

    with open("/var/www/parser/TestFiles/1.caff", "rb") as f:
        data['file'] = (BytesIO(f.read()), "1.caff")
    resp = client.post("/caff/upload", headers={"Authorization": "Bearer " + token}, content_type='multipart/form-data',
                       data=data)
    print(resp.data)
    assert resp.status_code == 201

    uploaded_caff = CaffFile.objects.get(caffName=data['name'])
    assert uploaded_caff is not None
    assert uploaded_caff.uploaderName == "testuser"


@pytest.mark.username("testuser")
@pytest.mark.password("test1234")
def test_caff_download(client, token):
    resp = client.get("/caff/download/1", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 404 and resp.is_json and "File does not exist" in resp.json["errorMessage"]

    resp = client.get("/caff/download/61a559807aa83d946960d4f2", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200


@pytest.mark.username("testuser")
@pytest.mark.password("test1234")
def test_caff_search(client, token):

    resp = client.get("/caff/search?page=0", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 400

    resp = client.get("/caff/search?page=2", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 400

    resp = client.get("/caff/search?perpage=0", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 400

    resp = client.get("/caff/search?username=Test Creator", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200

    resp = client.get("/caff/search?uploaderName=testuser", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200

    resp = client.get("/caff/search?creationDate=" + str(datetime(2020, 7, 2, 14, 50, 0, 0)), headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200

    resp = client.get("/caff/search?uploadDate=" + str(datetime(2021,11, 29, 22, 40, 46, 35)), headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200
