import pytest
from datetime import datetime
from fixtures import client, token
from io import BytesIO
from flaskapp.database.models import Comment


@pytest.mark.username("testuser")
@pytest.mark.password("test1234")
def test_comments(client, token):
    resp = client.get("/comment?caffId=00a000000aa00a000000a0a0", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 404 and resp.is_json and "file does not exist" in resp.json["errorMessage"]

    resp = client.get("/comment?caffId=61a559807aa83d946960d4f2", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and len(resp.json["comments"]) == 0

    resp = client.post("/comment?caffId=61a559807aa83d946960d4f2", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 400 and resp.is_json and "body cannot be empty" in resp.json["errorMessage"]

    data = {
        'caffId': '61a559807aa83d946960d4f2',
        'comment': 'Test Comment'
    }
    resp = client.post("/comment", headers={"Authorization": "Bearer " + token},
                       json=data)
    assert resp.status_code == 200 and resp.is_json and "comment created successful" in resp.json["message"]

    resp = client.get("/comment?caffId=61a559807aa83d946960d4f2", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and resp.is_json and len(resp.json['comments']) == 1

    resp = client.delete("/comment", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 403 and resp.is_json and "forbidden interaction" in resp.json["errorMessage"]

    data['caffId'] = None
    resp = client.post("/comment", headers={"Authorization": "Bearer " + token},
                       json=data)
    assert resp.status_code == 400 and resp.is_json and "caffId cannot be empty" in resp.json["errorMessage"]

    data['comment'] = None
    resp = client.post("/comment", headers={"Authorization": "Bearer " + token},
                       json=data)
    assert resp.status_code == 400 and resp.is_json and "comment cannot be empty" in resp.json["errorMessage"]

    data['caffId'] = '61a559807aa83d946960d4f2'
    data['comment'] = ''
    for i in range(1, 210):
        data['comment'] += 'a'
    resp = client.post("/comment", headers={"Authorization": "Bearer " + token},
                       json=data)
    assert resp.status_code == 400 and resp.is_json and "comment too long" in resp.json["errorMessage"]

    data['caffId'] = '00a000000aa00a000000a0a0'
    data['comment'] = 'Test comment'
    resp = client.post("/comment", headers={"Authorization": "Bearer " + token},
                       json=data)
    assert resp.status_code == 400 and resp.is_json and "file does not exist" in resp.json["errorMessage"]


@pytest.mark.username("testadmin")
@pytest.mark.password("DLee7ono7LVT5qiH7bkAxWgDfeegMNSj")
def test_comments_delete_admin(client, token):
    data = {
        'caffId': '61a559807aa83d946960d4f2',
        'comment': 'Test Comment'
    }
    resp = client.post("/comment", headers={"Authorization": "Bearer " + token},
                       json=data)
    assert resp.status_code == 200 and resp.is_json and "comment created successful" in resp.json["message"]

    resp = client.delete("/comment", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 400 and resp.is_json and "caff id cannot be empty" in resp.json["errorMessage"]

    resp = client.delete("/comment?caffId=61a559807aa83d946960d4f2", headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 400 and resp.is_json and "comment id cannot be empty" in resp.json["errorMessage"]

    resp = client.delete("/comment?caffId=61a559807aa83d946960d4f2&commentId=100",
                         headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 400 and resp.is_json and "comment not found" in resp.json["errorMessage"]

    resp = client.delete("/comment?caffId=61a559807aa83d946960d4f2&commentId=0",
                         headers={"Authorization": "Bearer " + token})
    assert resp.status_code == 200 and resp.is_json and "comment deleted successful" in resp.json["message"]
