from pymongo import MongoClient
from pathlib import Path
from flaskapp.database.models import User, CaffFile
import os
import subprocess


def init(mongo_uri):
    """Initialize the mongodb for testing

    Params
    ------
    mongo_uri: URI of the mongodb to connect to
    """

    client = MongoClient(mongo_uri)
    db = client.CaffDatabase
    collections = ["user", "caff_file"]
    for coll in collections:
        db.drop_collection(coll)

    script_dir = Path(__file__).parent.absolute()
    initscript = os.path.join(script_dir, "init_db.js")
    subprocess.run(["mongo", mongo_uri, initscript])
    for cl in [User, CaffFile]:
        cl.ensure_indexes()
