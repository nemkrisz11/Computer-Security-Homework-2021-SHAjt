db.createUser({
    user: "flaskuser",
    pwd: "aGfuEXJRYwBgDEmo8aXK8wY2sCkwQ9N6",
    roles: [ { role: "readWrite", db: "CaffDatabase"} ]
});