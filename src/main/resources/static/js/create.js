getMenus();

window.onload = function() {
    this.loadMenus();
    let time = this.createTimeBox();
    time.id = "time";
    time.name = "time";
    this.document.getElementById("time-li").appendChild(time);
}

function create() {
    let url = "/api/meeting/create";
    let headers = {
        "Authorization": "Meeting " + localStorage.token,
        'content-type': 'application/json'
    };

    let name = createForm.name.value;
    let time = stringifyTime(document.getElementById("time"));
    let location = createForm.location.value;
    let star = createForm.star.value;
    let hotel = createForm.hotel.value;
    let comment = createForm.comment.value;
    let needName = createForm.needName.checked;
    let needOrganization = createForm.needOrganization.checked;
    let needIdCard = createForm.needIdCard.checked;
    let needParticipateTime = createForm.needParticipateTime.checked;
    let needGender = createForm.needGender.checked;

    if (name.length === 0) {
        disposeHint('会议名不能为空！');
    } else if (location.length === 0) {
        disposeHint('地点不能为空！');
    }

    let data = JSON.stringify({
        name: name,
        time: time,
        location: location,
        star: star,
        hotel: hotel,
        comment: comment,
        needName: needName,
        needOrganization: needOrganization,
        needIdCard: needIdCard,
        needParticipateTime: needParticipateTime,
        needGender: needGender
    });

    fetch(url, {
        body: data,
        headers: headers,
        method: "POST"
    })
    .then(response => response.json())
    .then(function(json) {
        disposeHint(json.message);
    })
}