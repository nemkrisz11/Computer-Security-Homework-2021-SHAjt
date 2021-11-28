db.createCollection("user")
db.createCollection("caff_file")

users = [
    {
        "name": "testuser",
        "password": "$argon2id$v=19$m=102400,t=2,p=8$XJMr9QHZOL1rcYnvVUqN5g$ZIXv2AfS5eJ3qadYFsUMsA" // "test1234"
    }
];

db.user.insertMany(users);