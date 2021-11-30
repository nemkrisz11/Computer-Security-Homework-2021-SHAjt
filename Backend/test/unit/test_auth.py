import pytest
from fixtures import client, token


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

    # Password too short
    resp = client.post("/user/register", json=data)
    assert resp.status_code == 400 and resp.is_json
    assert "too short" in resp.json["errorMessage"]

    # Password too weak
    data["password"] = "password1234"
    resp = client.post("/user/register", json=data)
    assert resp.status_code == 400 and resp.is_json
    assert "too weak" in resp.json["errorMessage"]

    # Password not given
    resp = client.post("/user/register", json={"username": "tim123"})
    assert resp.status_code == 400 and resp.is_json
    assert "password cannot be empty" in resp.json["errorMessage"]

    # Username not given
    resp = client.post("/user/register", json={"password": "asdasdasdasd1231ASD@äđĐ"})
    assert resp.status_code == 400 and resp.is_json
    assert "username cannot be empty" in resp.json["errorMessage"]

    # Username is taken
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

    # Invalid password
    resp = client.post("/user/login", json=data)
    assert resp.status_code == 400 and resp.is_json
    assert "invalid" in resp.json["errorMessage"]

    # Invalid username
    data["username"] = "asd"
    data["password"] = "test1234"
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
                       json={"password": "test5678ASD&@"})
    assert resp.status_code == 200 and resp.is_json and "successful" in resp.json["message"]

    resp = client.post("/user/login", json={
        "username": "testuser",
        "password": "test5678ASD&@"})
    assert resp.status_code == 200 and resp.is_json
    assert len(resp.json["token"]) > 20


@pytest.mark.username("testuser")
@pytest.mark.password("test1234")
def test_invalid_password_change_self(client, token):
    # Attempt to change other user's password
    resp = client.post("/user/password", headers={"Authorization": "Bearer " + token},
                       json={
                           "username": "asdasd",
                           "password": "test5678ASD&@"})
    assert resp.status_code == 403 and resp.is_json and "forbidden" in resp.json["errorMessage"]

    # New password too short
    resp = client.post("/user/password", headers={"Authorization": "Bearer " + token},
                       json={"password": "t"})
    assert resp.status_code == 400 and resp.is_json and "too short" in resp.json["errorMessage"]

    # No password given
    resp = client.post("/user/password", headers={"Authorization": "Bearer " + token},
                       json={"qwert": "t"})
    assert resp.status_code == 400 and resp.is_json and "password cannot be empty" in resp.json["errorMessage"]


@pytest.mark.username("testadmin")
@pytest.mark.password("DLee7ono7LVT5qiH7bkAxWgDfeegMNSj")
def test_password_change_self_admin(client, token):
    resp = client.post("/user/password", headers={"Authorization": "Bearer " + token},
                       json={"password": "test5678ASD&@"})
    assert resp.status_code == 200 and resp.is_json and "successful" in resp.json["message"]

    resp = client.post("/user/login", json={
                           "username": "testadmin",
                           "password": "test5678ASD&@"})
    assert resp.status_code == 200 and resp.is_json
    assert len(resp.json["token"]) > 20


@pytest.mark.username("testadmin")
@pytest.mark.password("DLee7ono7LVT5qiH7bkAxWgDfeegMNSj")
def test_password_change_other_user(client, token):
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
