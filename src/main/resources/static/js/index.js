var stronge = window.localStorage;

var url = "/api/user/menus";

window.onload = function() {
    var select_ul = document.getElementById("select-ul");
    var login_a = this.document.getElementById("login_a");

    var profile_li = this.document.createElement("li");
    var profile_a = this.document.createElement("a");

    profile_li.id = "profile_li";
    profile_a.id = "profile_a";
    profile_a.innerHTML = "个人信息";
    profile_li.appendChild(profile_a);

    fetch(url, {
        headers: {
            "Authorization": "Meeting " + stronge.token
        },
        method: "GET"
    })
    .then(response => response.json())
    .then(function(json) {
        if (json.status == 1) {
            var data = json.data;

            select_ul.innerHTML = "";

            for (let index = 0; index < data.length; index++) {
                const element = data[index];
                let li = this.document.createElement("li");
                var a = this.document.createElement("a");
                li.appendChild(a);

                a.innerHTML = element.name;
                a.href = element.url;

                select_ul.appendChild(li); 
            }
        }
    })
}

