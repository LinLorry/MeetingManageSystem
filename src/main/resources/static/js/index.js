let stronge = window.localStorage;

let url = "/api/user/menus";

if (stronge.menus == null || stronge.menus == "") {
    loadMenus();
}

window.onload = function() {
    let select_ul = document.getElementById("select-ul");
    let menus = this.JSON.parse(stronge.menus);

    if (menus.length != 0) {
        select_ul.innerHTML = "";
        menus.forEach(element => {
            let li = this.document.createElement("li");
            let a = this.document.createElement("a");
            li.appendChild(a);
    
            a.innerHTML = element.name;
            a.href = element.url;
    
            select_ul.appendChild(li);
        });
    }
}

function loadMenus() {
    let url = "/api/user/menus";

    fetch(url, {
        headers: {
            "Authorization": "Meeting " + window.localStorage.token
        },
        method: "GET"
    })
    .then(response => response.json())
    .then(function(json) {
        window.localStorage.menus = JSON.stringify(json.data);
    });
}
