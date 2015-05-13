$(document).ready(function() {
    $("#addUser").submit(function(event) {
        event.preventDefault();

        user = {
            username: $("#username").val(),
            emailAddress: $("#emailAddress").val(),
            realName: $("#realName").val()
        }

        $.post("/addUser", {user: JSON.stringify(user)}, function(data) {
            if (data === "true") {
                refreshUsers();
            } else {
                alert("Didn't add user?");
            }
        });
    });

    $("#addNote").submit(function(event) {
        event.preventDefault();

        note = {
            name: $("#title").val(),
            text: $("#noteText").val()
        }

        console.log(note);

        $.post("/addNote", note, function(data) {
            console.log(data);
            if (data === "true") {
                refreshNotes();
            } else {
                alert("Couldn't add note?");
            }
        });
    });

    refreshUsers();
    refreshNotes();
});


var refreshUsers = function() {
    $.getJSON("/getUsers", function(data) {
        console.log(data);
        $("#usersList").html("");

        _.forEach(data, function(value, key) {
            $("<li/>").append(
                $("<strong/>").text("Username: ")
            ).append(value.username).append($("<br/>")).append(
                $("<strong/>").text("Email Address: ")
            ).append(value.emailAddress).appendTo($("#usersList"));
                //.text(key + " : " + JSON.stringify(value)).appendTo($("#usersList"));
        });
    });
}

var refreshNotes = function() {
    $.getJSON("/getNotes", function(data) {
        console.log(data);
        $("#notesList").html("");

        _.forEach(data, function(value, key) {
            $("<li/>").append(
                $("<h2/>").text(key)
            ).append(
                $("<pre/>").text(value)
            ).appendTo($("#notesList"));
        });
    });
}
