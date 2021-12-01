import pytest
from datetime import datetime
from fixtures import client, token
from io import BytesIO
from flaskapp.database.models import CaffFile


@pytest.mark.username("testuser")
@pytest.mark.password("test1234")
def test_caff_upload(client, token):
    data = {'name': 'this is a name'}

    with open("/var/www/parser/TestFiles/1.caff", "rb") as f:
        data['file'] = (BytesIO(f.read()), "1.caff")
    resp = client.post("/caff/upload", headers={"Authorization": "Bearer " + token}, content_type='multipart/form-data',
                       data=data)
    assert resp.status_code == 201

    uploaded_caff = CaffFile.objects.get(caffName=data['name'])
    assert uploaded_caff is not None
    assert uploaded_caff.uploaderName == "testuser"


@pytest.mark.username("testuser")
@pytest.mark.password("test1234")
def test_caff_upload_misuse(client, token):
    data = {'name': 'this is a name'}

    # No file sent
    resp = client.post("/caff/upload", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 400 and resp.is_json and "file not in request" in resp.json["errorMessage"]

    # Random data sent
    data["file"] = (BytesIO(b"asdasdasdasdasdasdasdasdas"), "1.caff")
    resp = client.post("/caff/upload", headers={"Authorization": "Bearer " + token}, content_type='multipart/form-data',
                       data=data)
    assert resp.status_code == 400 and resp.is_json and "invalid file format" in resp.json["errorMessage"]

    # No name parameter
    resp = client.post("/caff/upload", headers={"Authorization": "Bearer " + token}, content_type='multipart/form-data',
                       data={'file': (BytesIO(b"asdasdasdasdasdasdasdasdas"), "1.caff")})
    assert resp.status_code == 400 and resp.is_json and "invalid parameters" in resp.json["errorMessage"]

    # File name not given
    resp = client.post("/caff/upload", headers={"Authorization": "Bearer " + token}, content_type='multipart/form-data',
                       data={'name': 'something', 'file': (BytesIO(b"asdasdasdasdasdasdasdasdas"), "")})
    assert resp.status_code == 400 and resp.is_json and "file not in request" in resp.json["errorMessage"]

    # Empty file sent
    resp = client.post("/caff/upload", headers={"Authorization": "Bearer " + token}, content_type='multipart/form-data',
                       data={'file': (b"", "1.caff")})
    assert resp.status_code == 400 and resp.is_json and "invalid parameters" in resp.json["errorMessage"]

    # File size over 50 MB limit
    data["file"] = (BytesIO(b"a" * 1024 * 1024 * 60), "1")
    resp = client.post("/caff/upload", headers={"Authorization": "Bearer " + token}, content_type='multipart/form-data',
                       data=data)
    assert resp.status_code == 413


@pytest.mark.username("testuser")
@pytest.mark.password("test1234")
def test_caff_download(client, token):
    resp = client.get("/caff/download/61a559807aa83d946960d4f2", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200


@pytest.mark.username("testuser")
@pytest.mark.password("test1234")
def test_caff_download_misuse(client, token):
    resp = client.get("/caff/download/1", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 404 and resp.is_json and "file does not exist" in resp.json["errorMessage"]

    resp = client.get("/caff/download/61a559807ab83d946960d412", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 404 and resp.is_json and "file does not exist" in resp.json["errorMessage"]


@pytest.mark.username("testuser")
@pytest.mark.password("test1234")
def test_caff_search(client, token):
    resp = client.get("/caff/search?page=1", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and len(resp.json['caffs']) == 1

    resp = client.get("/caff/search?searchTerm=test", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and len(resp.json['caffs']) == 1

    resp = client.get("/caff/search?username=Test Creator", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and len(resp.json['caffs']) == 1

    resp = client.get("/caff/search?uploaderName=testuser", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and len(resp.json['caffs']) == 1

    resp = client.get("/caff/search?uploaderName=testuser&username=Test Creator", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and len(resp.json['caffs']) == 1

    resp = client.get("/caff/search?uploaderName=testuser&username=asd", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and len(resp.json['caffs']) == 0

    # 2020-07-02 13:14:14
    resp = client.get("/caff/search?creationDate=1593688454000", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and len(resp.json['caffs']) == 1

    # 2020-07-4 00:00:00
    resp = client.get("/caff/search?creationDate=1593813600000", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and len(resp.json['caffs']) == 0

    # 2021-11-29 23:00:14
    resp = client.get("/caff/search?uploadDate=1638223214000", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and len(resp.json['caffs']) == 1

    # 2021-11-26 23:00:14
    resp = client.get("/caff/search?uploadDate=1637964014000", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and len(resp.json['caffs']) == 0


@pytest.mark.username("testuser")
@pytest.mark.password("test1234")
def test_caff_search_misuse(client, token):
    resp = client.get("/caff/search?page=0", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 400

    resp = client.get("/caff/search?page=2", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 400

    resp = client.get("/caff/search?perpage=0", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 400


@pytest.mark.username("testuser")
@pytest.mark.password("test1234")
def test_caff_preview(client, token):
    resp = client.get("/caff/61a559807aa83d946960d4f2", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and resp.json is not None


@pytest.mark.username("testuser")
@pytest.mark.password("test1234")
def test_caff_preview_misuse(client, token):
    resp = client.get("/caff/00a000807aa83d946960d4f2", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 404 and resp.json and resp.is_json and "file does not exist" in resp.json["errorMessage"]


@pytest.mark.username("testuser")
@pytest.mark.password("test1234")
def test_caff_delete_without_privilege(client, token):
    resp = client.delete("/caff/61a559807aa83d946960d4f2", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 403 and "forbidden interaction" in resp.json["errorMessage"]


@pytest.mark.username("testadmin")
@pytest.mark.password("DLee7ono7LVT5qiH7bkAxWgDfeegMNSj")
def test_caff_delete(client, token):
    resp = client.delete("/caff/00aabbccddeeffgghhiijjkk", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 404 and resp.json and resp.is_json and "file does not exist" in resp.json["errorMessage"]

    resp = client.delete("/caff/61a559807aa83d946960d4f2", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and resp.json and "CaffFile delete successful" in resp.json["message"]
