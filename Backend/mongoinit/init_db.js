db.createCollection("user")
db.createCollection("caff_file")

users = [
    {
        "name": "testuser",
        "password": "$argon2id$v=19$m=102400,t=2,p=8$XJMr9QHZOL1rcYnvVUqN5g$ZIXv2AfS5eJ3qadYFsUMsA" // "test1234"
    },
    {
        "name": "testadmin",
        "password": "$argon2id$v=19$m=102400,t=2,p=8$QoYzR+8V6YmGrLW2mDGrYA$wUOOJdqDmkFXj051P/fQCg", // DLee7ono7LVT5qiH7bkAxWgDfeegMNSj
        "isAdmin": true
    }
];

db.user.insertMany(users);