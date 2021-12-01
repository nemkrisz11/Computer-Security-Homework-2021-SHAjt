import pytest
from datetime import datetime
from fixtures import client, token
from io import BytesIO
from flaskapp.database.models import Comment


@pytest.mark.username("testuser")
@pytest.mark.password("test1234")
def test_comments(client, token):
    data = {
        'caffId': '61a559807aa83d946960d4f2',
        'comment': 'Test Comment'
    }

    resp = client.get("/comment?caffId=61a559807aa83d946960d4f2", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and resp.is_json and len(resp.json['comments']) == 0

    resp = client.post("/comment", headers={"Authorization": "Bearer " + token},
                       json=data)
    assert resp.status_code == 200 and resp.is_json and "comment created successful" in resp.json["message"]

    resp = client.get("/comment?caffId=61a559807aa83d946960d4f2", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and resp.is_json and len(resp.json['comments']) == 1


@pytest.mark.username("testuser")
@pytest.mark.password("test1234")
def test_comments_misuse(client, token):
    # Bad CaffId
    resp = client.get("/comment?caffId=00a000000aa00a000000a0a0", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 404 and resp.is_json and "file does not exist" in resp.json["errorMessage"]

    # No comment body
    resp = client.post("/comment?caffId=61a559807aa83d946960d4f2", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 400 and resp.is_json and "body cannot be empty" in resp.json["errorMessage"]

    # CaffId is none
    resp = client.post("/comment", headers={"Authorization": "Bearer " + token},
                       json={'caffId': None, 'comment': 'Test Comment'})
    assert resp.status_code == 400 and resp.is_json and "caffId cannot be empty" in resp.json["errorMessage"]

    # No such CAFF
    resp = client.post("/comment", headers={"Authorization": "Bearer " + token},
                       json={'caffId': '61a559807aa83d946960d4ff', 'comment': 'Test Comment'})
    assert resp.status_code == 400 and resp.is_json and "file does not exist" in resp.json["errorMessage"]

    # Comment is none
    resp = client.post("/comment", headers={"Authorization": "Bearer " + token},
                       json={'caffId': '61a559807aa83d946960d4f2', 'comment': None})
    assert resp.status_code == 400 and resp.is_json and "comment cannot be empty" in resp.json["errorMessage"]

    # Comment too long
    resp = client.post("/comment", headers={"Authorization": "Bearer " + token},
                       json={'caffId': '61a559807aa83d946960d4f2', 'comment': "a" * 210})
    assert resp.status_code == 400 and resp.is_json and "comment too long" in resp.json["errorMessage"]

    # Incorrect page and per-page numbers
    resp = client.get("/comment?caffId=61a559807aa83d946960d4f2&page=0", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 400 and resp.is_json and "invalid page number" in resp.json["errorMessage"]

    resp = client.get("/comment?caffId=61a559807aa83d946960d4f2&perpage=0", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 400 and resp.is_json and "invalid per page number" in resp.json["errorMessage"]

    resp = client.get("/comment?caffId=61a559807aa83d946960d4f2&perpage=-1", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 400 and resp.is_json and "invalid per page number" in resp.json["errorMessage"]

    resp = client.get("/comment?caffId=61a559807aa83d946960d4f2&page=50000", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and resp.is_json and len(resp.json['comments']) == 0


@pytest.mark.username("testuser")
@pytest.mark.password("test1234")
def test_comments_delete_without_privilege(client, token):
    data = {
        'caffId': '61a559807aa83d946960d4f2',
        'comment': 'Test Comment'
    }
    resp = client.post("/comment", headers={"Authorization": "Bearer " + token},
                       json=data)
    assert resp.status_code == 200 and resp.is_json and "comment created successful" in resp.json["message"]

    resp = client.delete("/comment?caffId=61a559807aa83d946960d4f2&commentId=0",
                         headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 403 and resp.is_json and "forbidden interaction" in resp.json["errorMessage"]


@pytest.mark.username("testadmin")
@pytest.mark.password("DLee7ono7LVT5qiH7bkAxWgDfeegMNSj")
def test_comments_delete(client, token):
    data = {
        'caffId': '61a559807aa83d946960d4f2',
        'comment': 'Test Comment'
    }
    resp = client.post("/comment", headers={"Authorization": "Bearer " + token},
                       json=data)
    assert resp.status_code == 200 and resp.is_json and "comment created successful" in resp.json["message"]

    resp = client.delete("/comment?caffId=61a559807aa83d946960d4f2&commentId=0",
                         headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and resp.is_json and "comment deleted successful" in resp.json["message"]


@pytest.mark.username("testadmin")
@pytest.mark.password("DLee7ono7LVT5qiH7bkAxWgDfeegMNSj")
def test_comments_delete_misuse(client, token):
    data = {
        'caffId': '61a559807aa83d946960d4f2',
        'comment': 'Test Comment'
    }
    resp = client.post("/comment", headers={"Authorization": "Bearer " + token},
                       json=data)
    assert resp.status_code == 200 and resp.is_json and "comment created successful" in resp.json["message"]

    # No caff id in parameters
    resp = client.delete("/comment", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 400 and resp.is_json and "caff id cannot be empty" in resp.json["errorMessage"]

    # No comment id in parameters
    resp = client.delete("/comment?caffId=61a559807aa83d946960d4f2", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 400 and resp.is_json and "comment id cannot be empty" in resp.json["errorMessage"]

    # No such comment
    resp = client.delete("/comment?caffId=61a559807aa83d946960d4f2&commentId=100",
                         headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 400 and resp.is_json and "comment not found" in resp.json["errorMessage"]

    # No such CAFF
    resp = client.delete("/comment?caffId=123&commentId=0",
                         headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 400 and resp.is_json and "comment not found" in resp.json["errorMessage"]