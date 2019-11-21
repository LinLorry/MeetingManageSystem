getMenus();

window.onload = function() {
    this.getProfile();
    this.loadMenus();

    if  (this.localStorage.profile === this.undefined || this.localStorage.profile.length === 0) {
        this.location.href = "/login.html";
    }

    let profile = this.JSON.parse(this.localStorage.profile);
    let profileForm = this.document.getElementById("profile-form");

    profileForm.name.value = profile.name;
    this.console.log(profile.gender);
    if (profile.gender) {
        profileForm.female.checked = true;
    } else {
        profileForm.male.checked = true;
    }

    profileForm.organization.value = profile.organization;
    profileForm.phoneNumber.value = profile.phoneNumber;
    profileForm.idCard.value = profile.idCard;
}

function editProfile() {
    let stronge = window.localStorage;
    let url = "/api/user/profile";

    let profileForm = this.document.getElementById("profile-form");

    let data = {
        name: profileForm.name.value,
        gender: profileForm.female.checked,
        organization: profileForm.organization.value,
        phoneNumber: profileForm.phoneNumber.value,
        idCard: profileForm.idCard.value
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
        disposeHint(json.message);
        if (json.status === 1) {
            window.localStorage.profile = JSON.stringify(json.data);
        }
    })
}

