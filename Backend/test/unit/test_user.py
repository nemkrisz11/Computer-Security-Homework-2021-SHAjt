import pytest
from fixtures import client, token
from flaskapp.database.models import User


def test_valid_register(client):
    resp = client.post("/user/register",
                       json={"name": "thisisauniqueuser34958",
                             "password": "12345678"
                             })

    assert resp.status_code == 200
    assert resp.is_json
    assert len(resp.json["id"]) > 0


def test_invalid_register(client):
    data = {
        "name": "thisisauniqueuser34958",
        "password": "123"
    }

    resp = client.post("/user/register", json=data)
    assert resp.status_code == 400 and resp.is_json
    assert "too short" in resp.json["errorMessage"]["password"]

    data["password"] = "12345678"
    data["name"] = "testuser"
    resp = client.post("/user/register", json=data)
    assert "already in use" in resp.json["errorMessage"]["name"]


def test_valid_login(client):
    resp = client.post("/user/login",
                       json={"name": "testuser",
                             "password": "test1234"})
    assert resp.status_code == 200
    assert resp.is_json
    assert len(resp.json["token"]) > 20


def test_invalid_login(client):
    data = {
        "name": "testuser",
        "password": "qwertyasd"
    }

    resp = client.post("/user/login", json=data)
    assert resp.status_code == 400
    assert resp.is_json
    assert "invalid" in resp.json["errorMessage"]

    data["name"] = "asd"
    resp = client.post("/user/login", json=data)
    assert resp.status_code == 400
    assert resp.is_json
    assert "invalid" in resp.json["errorMessage"]


@pytest.mark.name("testuser")
@pytest.mark.password("test1234")
def test_logout(client, token):
    resp = client.post("/user/logout", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and resp.is_json

    resp = client.post("/user/logout", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 401 and resp.is_json
    assert "revoked" in resp.json["msg"]
