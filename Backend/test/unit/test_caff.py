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

    # TODO: Check if parsed data is correct

    uploaded_caff = CaffFile.objects.get(caffName=data['name'])
    print(uploaded_caff.creator)
    assert uploaded_caff is not None
