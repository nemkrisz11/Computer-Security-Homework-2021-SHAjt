"""Fixtures are methods that set up, and restore a context for the test case.
See: https://docs.pytest.org/en/6.2.x/fixture.html
"""

import pytest
from flaskapp.app import *
from mongoinit.init_db import init


@pytest.fixture
def client():
    """Creates a test client for the application.
    Supports standard werkzeug stuff, e.g.:
    https://werkzeug.palletsprojects.com/en/2.0.x/test/
    """
    app = create_app()
    with app.test_client() as client:
        with app.app_context():
            init('mongodb://' + os.environ['MONGODB_USERNAME'] + ':' + os.environ[
                'MONGODB_PASSWORD'] + '@' + os.environ['MONGODB_HOSTNAME'] + ':' + os.environ['MONGODB_PORT'] + '/' + \
                 os.environ['MONGODB_DATABASE'])
        yield client


@pytest.fixture
def token(client, request):
    """Logs in the user with the name in pytest.mark.name, and
    yields an access token """

    rv = client.post("/user/login", json={
        "name": request.node.get_closest_marker("name").args[0],
        "password": request.node.get_closest_marker("password").args[0]
    })

    assert rv.is_json and "token" in rv.json
    token = rv.json["token"]
    yield token

    client.post("/user/logout", headers={"Authorization": "Bearer " + token})
