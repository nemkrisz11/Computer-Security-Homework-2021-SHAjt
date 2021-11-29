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

caffs = [
    { "caffName" : "testcaff",
      "caffAnimationImage" : { "duration" : 1000, "width" : 1000, "height" : 667, "caption" : "Beautiful scenery", "tags" : [ "landscape", "sunset", "mountains" ] },
      "numOfCiffs" : 2,
      "creator" : "Test Creator",
      "creationDate" : ISODate("2020-07-02T14:50:00Z"),
      "comments" : [],
      "uploaderName" : "testuser",
      "uploadDate" : ISODate("2021-11-29T22:40:46.350Z")
    }
]

db.caff_file.insertMany(caffs)
