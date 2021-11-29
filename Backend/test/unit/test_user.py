import pytest
from fixtures import client, token
from flaskapp.database.models import User


def test_valid_register(client):
    resp = client.post("/user/register",
                       json={"username": "thisisauniqueuser34958",
                             "password": "12345678"
                             })

    assert resp.status_code == 200 and resp.is_json
    assert len(resp.json["id"]) > 0


def test_invalid_register(client):
    data = {
        "username": "thisisauniqueuser34958",
        "password": "123"
    }

    resp = client.post("/user/register", json=data)
    assert resp.status_code == 400 and resp.is_json
    assert "too short" in resp.json["errorMessage"]["password"]

    data["password"] = "12345678"
    data["username"] = "testuser"
    resp = client.post("/user/register", json=data)
    assert "already in use" in resp.json["errorMessage"]["username"]


def test_valid_login(client):
    resp = client.post("/user/login",
                       json={"username": "testuser",
                             "password": "test1234"})
    assert resp.status_code == 200 and resp.is_json
    assert len(resp.json["token"]) > 20


def test_invalid_login(client):
    data = {
        "username": "testuser",
        "password": "qwertyasd"
    }

    resp = client.post("/user/login", json=data)
    assert resp.status_code == 400 and resp.is_json
    assert "invalid" in resp.json["errorMessage"]

    data["username"] = "asd"
    resp = client.post("/user/login", json=data)
    assert resp.status_code == 400 and resp.is_json
    assert "invalid" in resp.json["errorMessage"]


@pytest.mark.username("testuser")
@pytest.mark.password("test1234")
def test_logout(client, token):
    resp = client.post("/user/logout", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and resp.is_json

    resp = client.post("/user/logout", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 401 and resp.is_json
    assert "revoked" in resp.json["msg"]


@pytest.mark.name("testuser")
@pytest.mark.password("test1234")
def test_password_change_self(client, token):
    resp = client.post("/user/password", headers={"Authorization": "Bearer " + token},
                       json={
                           "name": "asdasd",
                           "password": "test5678"})
    assert resp.status_code == 403 and resp.is_json and "forbidden" in resp.json["errorMessage"]

    resp = client.post("/user/password", headers={"Authorization": "Bearer " + token},
                       json={"password": "t"})
    assert resp.status_code == 400 and resp.is_json and "too short" in resp.json["errorMessage"]["password"]

    resp = client.post("/user/password", headers={"Authorization": "Bearer " + token},
                       json={"qwert": "t"})
    assert resp.status_code == 400 and resp.is_json and "required" in resp.json["errorMessage"]["password"]

    resp = client.post("/user/password", headers={"Authorization": "Bearer " + token},
                       json={"password": "test5678"})
    assert resp.status_code == 200 and resp.is_json and "successful" in resp.json["message"]


@pytest.mark.name("testadmin")
@pytest.mark.password("DLee7ono7LVT5qiH7bkAxWgDfeegMNSj")
def test_password_change_admin(client, token):
    resp = client.post("/user/password", headers={"Authorization": "Bearer " + token},
                       json={
                           "name": "testuser",
                           "password": "test5678"})
    assert resp.status_code == 200 and resp.is_json and "successful" in resp.json["message"]

    resp = client.post("/user/login", json={
                           "name": "testuser",
                           "password": "test1234"})
    assert resp.status_code == 400 and resp.is_json
    assert "invalid" in resp.json["errorMessage"]

    resp = client.post("/user/login", json={
                           "name": "testuser",
                           "password": "test5678"})
    assert resp.status_code == 200 and resp.is_json
    assert len(resp.json["token"]) > 20
