import pytest
from fixtures import client, token
from flaskapp.database.models import User


@pytest.mark.username("testuser")
@pytest.mark.password("test1234")
def test_user_list(client, token):
    resp = client.get("/user/", headers={"Authorization": "Bearer " + token}, query_string={"page": 1, "perpage": 10})
    assert resp.status_code == 200 and resp.is_json
    assert len(resp.json["users"]) >= 2
    assert resp.json["totalPages"] > 0

    resp = client.get("/user/", headers={"Authorization": "Bearer " + token},
                      query_string={"page": 1, "perpage": 10000000})
    assert resp.status_code == 200 and resp.is_json
    assert len(resp.json["users"]) >= 2
    assert resp.json["totalPages"] > 0

    resp = client.get("/user/", headers={"Authorization": "Bearer " + token}, query_string={"page": 4, "perpage": 10})
    assert resp.status_code == 400 and resp.is_json and "invalid page number" in resp.json["errorMessage"]

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
