function login() {
    var username = loginBox.username.value;
    var password = loginBox.password.value;
    var data = {
        username: username,
        password: password
    };

    url = "/api/user/token";

    fetch(url, {
        body: JSON.stringify(data),
        headers: {
            'content-type': 'application/json'
        },
        method: "POST"
    })
    .then(response => response.json())
    .then(function(data) {
        if (data.status == 1) {
            window.localStorage.token = data.token;
            window.location.href = "/index.html";
        } else {
            var hint = document.getElementById("hint");
            hint.style.display = "block"; 
            hint.innerHTML = data.message;
        } 
    });
}