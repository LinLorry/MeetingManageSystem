getMenus();

window.onload = function() {
    loadMenus();

    let stronge = window.localStorage;
    let url = "/api/user/profile";

    let profileForm = this.document.getElementById("profile-form");

    fetch(url, {
        headers: {
            "Authorization": "Meeting " + stronge.token
        },
        method: "GET"
    })
    .then(function(response) {
        if (response.ok) {
            return response.json();
        }
        window.location.href = "/login.html";
    })
    .then(function(json) {
        if (json.status == 1) {
            const data = json.data;
            profileForm.name.value = data.name;
            if (data.sex == 'male') {
                profileForm.male.checked = true;
            } else {
                profileForm.female.checked = true;
            }

            profileForm.organization.value = data.organization;
            profileForm.phoneNumber.value = data.phoneNumber;
        }
    })
}

function editProfile() {
    let stronge = window.localStorage;
    let url = "/api/user/profile";

    let profileForm = this.document.getElementById("profile-form");

    let data = {
        name: profileForm.name.value,
        sex: profileForm.male.checked ? 1 : 2,
        organization: profileForm.organization.value,
        phoneNumber: profileForm.phoneNumber.value
    }

    fetch(url, {
        body: JSON.stringify(data),
        headers: {
            'content-type': 'application/json',
            "Authorization": "Meeting " + stronge.token
        },
        method: "POST"
    })
    .then(response => response.json())
    .then(function(json) {
        var hint = document.getElementById("hint");
        hint.style.display = "block"; 
        hint.innerHTML = json.message;
    })
}

