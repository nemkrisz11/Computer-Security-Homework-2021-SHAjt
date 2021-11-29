import pytest
from fixtures import client, token
from flaskapp.database.models import User


def test_valid_register(client):
    resp = client.post("/user/register",
                       json={"username": "thisisauniqueuser34958",
                             "password": "12345678Asd&123ASD"
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
    assert "too short" in resp.json["errorMessage"]

    data["password"] = "12345678Asd&123ASD"
    data["username"] = "testuser"
    resp = client.post("/user/register", json=data)
    assert "taken" in resp.json["errorMessage"]["username"]


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


@pytest.mark.username("testuser")
@pytest.mark.password("test1234")
def test_password_change_self(client, token):
    resp = client.post("/user/password", headers={"Authorization": "Bearer " + token},
                       json={
                           "username": "asdasd",
                           "password": "test5678ASD&@"})
    assert resp.status_code == 403 and resp.is_json and "forbidden" in resp.json["errorMessage"]

    resp = client.post("/user/password", headers={"Authorization": "Bearer " + token},
                       json={"password": "t"})
    assert resp.status_code == 400 and resp.is_json and "too short" in resp.json["errorMessage"]

    resp = client.post("/user/password", headers={"Authorization": "Bearer " + token},
                       json={"qwert": "t"})
    assert resp.status_code == 400 and resp.is_json and "password cannot be empty" in resp.json["errorMessage"]

    resp = client.post("/user/password", headers={"Authorization": "Bearer " + token},
                       json={"password": "test5678ASD&@"})
    assert resp.status_code == 200 and resp.is_json and "successful" in resp.json["message"]

    resp = client.post("/user/login", json={
                           "username": "testuser",
                           "password": "test5678ASD&@"})
    assert resp.status_code == 200 and resp.is_json
    assert len(resp.json["token"]) > 20


@pytest.mark.username("testadmin")
@pytest.mark.password("DLee7ono7LVT5qiH7bkAxWgDfeegMNSj")
def test_password_change_admin(client, token):
    resp = client.post("/user/password", headers={"Authorization": "Bearer " + token},
                       json={
                           "username": "testuser",
                           "password": "test5678ASD&@"})
    assert resp.status_code == 200 and resp.is_json and "successful" in resp.json["message"]

    resp = client.post("/user/login", json={
                           "username": "testuser",
                           "password": "test1234"})
    assert resp.status_code == 400 and resp.is_json
    assert "invalid" in resp.json["errorMessage"]

    resp = client.post("/user/login", json={
                           "username": "testuser",
                           "password": "test5678ASD&@"})
    assert resp.status_code == 200 and resp.is_json
    assert len(resp.json["token"]) > 20


@pytest.mark.username("testuser")
@pytest.mark.password("test1234")
def test_user_list(client, token):
    resp = client.get("/user/", headers={"Authorization": "Bearer " + token}, query_string={"page": 1, "perpage": 10})
    assert resp.status_code == 200 and resp.is_json
    assert len(resp.json["users"]) >= 2
    assert resp.json["totalPages"] > 0

    resp = client.get("/user/", headers={"Authorization": "Bearer " + token}, query_string={"page": 1, "perpage": 10000000})
    assert resp.status_code == 200 and resp.is_json
    assert len(resp.json["users"]) >= 2
    assert resp.json["totalPages"] > 0

    resp = client.get("/user/", headers={"Authorization": "Bearer " + token}, query_string={"page": 4, "perpage": 10})
    assert resp.status_code == 200 and resp.is_json
    assert len(resp.json["users"]) == 0

    resp = client.get("/user/", headers={"Authorization": "Bearer " + token}, query_string={"page": 1, "perpage": -1})
    assert resp.status_code == 400 and resp.is_json

    resp = client.get("/user/", headers={"Authorization": "Bearer " + token}, query_string={"page": -11, "perpage": 10})
    assert resp.status_code == 400 and resp.is_json

    resp = client.get("/user/", headers={"Authorization": "Bearer " + token}, query_string={"page": 0, "perpage": 0})
    assert resp.status_code == 400 and resp.is_json


@pytest.mark.username("testuser")
@pytest.mark.password("test1234")
def test_user_get(client, token):
    resp = client.get("/user/testuser", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and resp.is_json
    assert resp.json["username"] == "testuser" and resp.json["isAdmin"] is False

    resp = client.get("/user/testadmin", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and resp.is_json
    assert resp.json["username"] == "testadmin" and resp.json["isAdmin"] is True

    resp = client.get("/user/124153asda", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 404 and resp.is_json and "user not found" in resp.json["errorMessage"]


@pytest.mark.username("testadmin")
@pytest.mark.password("DLee7ono7LVT5qiH7bkAxWgDfeegMNSj")
def test_delete_user(client, token):
    resp = client.delete("/user/testuser", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and resp.is_json and "user delete successful" in resp.json["message"]

    resp = client.post("/user/login", json={
                           "username": "testuser",
                           "password": "test1234"})
    assert resp.status_code == 400 and resp.is_json
    assert "invalid" in resp.json["errorMessage"]
