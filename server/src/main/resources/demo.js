var loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris ultrices risus fringilla, imperdiet erat quis, scelerisque tortor. Morbi convallis tristique dictum. Mauris facilisis vestibulum sapien ac dignissim. Donec blandit lacus eget faucibus fermentum. Phasellus vitae purus tristique, sollicitudin velit ac, lobortis est. Sed auctor odio mauris, vitae suscipit dolor fringilla id. Quisque in nulla in purus tristique facilisis. In ut congue nisi. Praesent nisi lorem, porttitor quis commodo at, iaculis vel nunc. Integer odio tellus, maximus ac porttitor eget, fermentum ac mi. Curabitur eros risus, varius at euismod ut, volutpat in ligula. Maecenas fringilla pretium arcu.";

function loadComments() {
    m.request({
        method: "GET",
        url: "http://localhost:1234/threads/1"}
             ).then(function(response) {
                 Data.comments = response.comments;
                 var toLoadUsers = [];
                 response.comments.forEach(function(c) {
                     if (toLoadUsers.indexOf(c.author) == -1 && !Data.users[c.author]) {
                         toLoadUsers.push(c.author);
                     };
                 });
                 loadUsers(toLoadUsers);
             });
}
function loadUsers(ids) {
    ids.forEach(function(id) {
        m.request({
            method: "GET",
            url: "http://localhost:1234/users/" + id
        }).then(function(response) {
            Data.users[id] = response.user;
        });
    });
};
var Data = {
    threadId: 1,
    comments: [],
    users: {},
    commentField: ""
};

function login(loginName) {
    var params = m.buildQueryString({
        login_name: loginName
    });
    return m.request({
        url: "/auth/login?" + params,
        method: "POST"
    }).then(function(response) {
        return response.user;
    });
}

function register(loginName, username) {
    var params = m.buildQueryString({
        login_name: loginName,
        username: username
    });
    return m.request({
        url: "/auth/register?" + params,
        method: "POST"
    }).then(function(response) {
        return response.user;
    });
}

function logout() {
    return m.request({
        url: "/auth/logout"
    });
}

function postComment(thread, content) {
    var params = m.buildQueryString({
        content: content
    });
    return m.request({
        url: "/threads/:thread/comments?" + params,
        method: "POST",
        data: {
            thread: thread
        }
    }).catch(function(e) {
        alert("Please log in");
    });
}

var MyComponent = {
    oninit: function(vnode) {
        loadComments();
    },
    view: function(vnode) {
        var commentInput = m("input[type=text]", {
            placeholder:"Enter comment",
            oninput: m.withAttr("value", function(t) {
                Data.commentField = t;
            })
        });
        var registerBtn = m("button", {onclick: function() {
            var loginName = window.prompt("Please enter your login name");
            if (!loginName) return;
            var username = window.prompt("Please enter your username");
            if (!username) return;
            register(loginName, username).then(function() {
                alert("Registered!");
            });
        }}, "Register");
        var postBtn = m("button", {onclick: function() {
            postComment(1, Data.commentField).then(function(response) {
                Data.commentField = "";
                Data.comments.push(response.comment);
            });
        }}, "Post Comment");
        return m("div", [
            m("h1", "Static Site Demo"),
            m("p", loremIpsum),
            m("h2", "Comments"),
            m("ul", Data.comments.map(function(c) {
                var name = Data.users[c.author] ? Data.users[c.author].loginName : "Loading...";
                return m("li", name + " says " + c.content);
            })),
            m("div", registerBtn),
            m("div", [
                commentInput,
                postBtn
            ])
        ]);
    }
};

m.mount(document.getElementById("app"), MyComponent);
