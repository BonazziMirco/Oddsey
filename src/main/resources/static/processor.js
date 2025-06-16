// displays the tab currentrly viewed
function setActiveTab(tab){
    document.getElementById(tab).setAttribute("class", "nav-link active")
}

// variation for the sport tab, which is a dropdown
function setActiveSportTab(tab){
    document.getElementById(tab).setAttribute("class", "nav-link active")
    document.getElementById("sport-tab-list").setAttribute("class", "nav-link active dropdown-toggle")
}

// sets the dashboard to the selected tab
function setDashboard(selection) {
    fetch(`/${selection}`)
        .then(function (response){
            if (response.status !== 200) {
                console.log(response.status);
                throw new Error("Errore nel loading del file JSON");}
            return response.text();
        })
        .then(function (html){
            document.getElementById("slate").innerHTML = html;
            if(selection === "review"){
                setDefaultRating();
            }
        })
        .catch(function(error){
            console.log(error);
            document.getElementById("slate").innerHTML = "Error loading dashboard";
        })
}

// shows or hides the password in the input field
function showPassword(id) {
    var x = document.getElementById(id);
    var y = document.getElementById(id+"Icon");
    if (x.type === "password") {
        x.type = "text";
        y.setAttribute("class", "bi bi-eye-slash fs-5");
    } else {
        x.type = "password";
        y.setAttribute("class", "bi bi-eye fs-5");
    }
}

// sets the rating for the review
// modifies the stars shown in the review form
function setRating(rating){
    var result = document.getElementById("rating");
    var emptyStar = "bi bi-star"
    var filledStar = "bi bi-star-fill"

    for (var i = 2; i <= rating; i++) {
        document.getElementById("rating-"+i+"-icon").setAttribute("class", filledStar);
    }
    for (var i = rating+1; i <= 5; i++) {
        document.getElementById("rating-"+i+"-icon").setAttribute("class", emptyStar);
    }

    result.value = rating;
}

// matches the passwords
// shows a message if the passwords match or not
function matchPassword() {
    document.getElementById('messageMatch').style.display = 'none';
    var result;
    if (document.getElementById('password').value === document.getElementById('confirmPassword').value) {
        if(document.getElementById('password').value !== "") {
            document.getElementById('messageMatch').style.display = 'block';
            document.getElementById('messageMatch').setAttribute("class", "valid");
            document.getElementById('messageMatch').innerHTML = 'Le password sono uguali';
        }
        result = true;
    } else {
        if(document.getElementById('password').value !== "") {
            document.getElementById('messageMatch').style.display = 'block';
            document.getElementById('messageMatch').setAttribute("class", "invalid");
            document.getElementById('messageMatch').innerHTML = 'Le password non sono uguali';
        }
        result = false;
    }
    return result;
}

// shows the password requirements
function showMessage() {
    document.getElementById("message").style.display = "block";
}

// hides the password requirements
function hideMessage() {
    document.getElementById("message").style.display = "none";
}

// validates the password according to the requirements
// modifies the message shown
function validatePassword() {
    var myInput = document.getElementById("password");
    var special = document.getElementById("special");
    var number = document.getElementById("number");
    var length = document.getElementById("length");
    var result = true;

    // Validate numbers
    var numbers = /[0-9]/g;
    let matches = myInput.value.match(numbers);
    if (matches && matches.length === 2) {
        number.classList.remove("invalid");
        number.classList.add("valid");
    } else {
        number.classList.remove("valid");
        number.classList.add("invalid");
        result = false;
    }

    // Validate special character
    var specialCharacters = /[^a-zA-Z0-9]/g;
    matches = myInput.value.match(specialCharacters);
    if (matches && matches.length === 1) {
        special.classList.remove("invalid");
        special.classList.add("valid");
    } else {
        special.classList.remove("valid");
        special.classList.add("invalid");
        result = false;
    }

    // Validate length
    if(myInput.value.length === 9) {
        length.classList.remove("invalid");
        length.classList.add("valid");
    } else {
        length.classList.remove("valid");
        length.classList.add("invalid");
        result = false;
    }

    return result;
}

// validates the password on input
// enables or disables the submit button based on the password validation
// variation for the login form as it is shorter
function validatePasswordLogin() {
    document.getElementById("submit-btn").disabled = !validatePassword();
}

// calculates the age based on the date of birth
function calculate_age(dob) {
    // Calculate the difference in milliseconds between the current date and the provided date of birth
    var diff_ms = Date.now() - dob.getTime();
    // Create a new Date object representing the difference in milliseconds and store it in the variable age_dt (age Date object)
    var age_dt = new Date(diff_ms);

    // Calculate the absolute value of the difference in years between the age Date object and the year 1970 (UNIX epoch)
    return Math.abs(age_dt.getUTCFullYear() - 1970);
}

// validates the credentials on input in the registration form
// checks the password, the match, the age and the year of birth
// enables or disables the submit button based on the validation
function validateCredentials(){
    var passwordCheck = validatePassword();
    var matchCheck = matchPassword();

    var year = document.getElementById("birthdate").value.split("-")[0];
    var currentYear = new Date().getFullYear();
    var yearOK = year >= 1900 && year <= currentYear;
    var ageCheck = calculate_age(new Date(document.getElementById("birthdate").value)) >= 18;

    if(!ageCheck && document.getElementById("birthdate").value !== ""){
        document.getElementById("ageAlert").style.display = "block";
    }else{
        document.getElementById("ageAlert").style.display = "none";
    }

    if (passwordCheck && matchCheck && ageCheck && yearOK) {
        document.getElementById("submit-btn").disabled = false;
    } else{
        document.getElementById("submit-btn").disabled = true;
    }
}

// validates the password on input in the password change form
function validatePasswordChange(){
    var passwordCheck = validatePassword();
    var matchCheck = matchPassword();
    if (matchCheck && passwordCheck){
        document.getElementById("submit-btn").disabled = false;
    } else {
        document.getElementById("submit-btn").disabled = true;
    }
}

// resets the form to its initial state
// disables the submit button and hides the password match message
function resetForm(){
    document.getElementById("submit-btn").disabled = true;
    document.getElementById("messageMatch").style.display = "none";
}

// sets the team selection options based on the selected sport
function setTeamSelection(sport){
    let dataList = document.getElementById("datalistOptions");
    dataList.innerHTML = "";
    let teamList = JSON.parse(document.getElementById("teamListElement").getAttribute("data-team-list"));
    teamList.forEach(element => {
        if (element.sport === sport) {
            const option = document.createElement("option");
            option.value = element.teamName;
            dataList.appendChild(option);
        }
    });
}

// fetches the news from the server and displays them in the news section
function getNews(){
    fetch(`/news`)
        .then(function (response){
            if (response.status !== 200) {
                console.log(response.status);
                throw new Error("Errore nel loading del file JSON");}
            return response.json();
        })
        .then(function (json) {
            const container = document.getElementById("newsList");
            container.innerHTML = '';
            json.forEach(news => {
                container.innerHTML += `
                    <div class="card">
                        <div class="card-body">
                            <h5 class="card-title">` + news.title + `</h5>
                            <p class="card-text newsContent">` + news.content + `</p>
                            <p class="text-muted card-text newsData"><small>` + news.date + `</small></p>
                        </div>
                    </div>
                `;
            })
        })
        .catch(function(error){
            console.log(error);
            document.getElementById("newsList").innerHTML = "Error loading news";
        })
}

// sets the interval for fetching news every 30 seconds
let newsIntervalSet = false;
function intervalNews(){
    getNews();
    if (!newsIntervalSet) {
        setInterval(getNews, 30000);
        newsIntervalSet = true;
    }
}

// promotes a user to admin
function promote(userId) {
    fetch(`/promote`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ userId: userId })
    })
        .then(function (response) {
            if (response.status !== 200) {
                console.log(response.status);
                throw new Error("Errore nel caricamento della promozione");
            }
            return response.text();
        })
        .then(function (html) {
            document.getElementById("slate").innerHTML = html;
        })
        .catch(function (error) {
            console.log(error);
            document.getElementById("slate").innerHTML = "Error promoting user";
        });
}

// changes the password of the user
function changePassword() {
    fetch(`/setNewPassword`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            oldPassword: document.getElementById("oldPassword").value,
            newPassword: document.getElementById("password").value
        })
    })
        .then(function (response) {
            if (response.status !== 200) {
                console.log(response.status);
                throw new Error("Errore nel caricamento della modifica della password");
            }
            return response.text();
        })
        .then(function (html) {
            document.getElementById("slate").innerHTML = html;
        })
        .catch(function (error) {
            console.log(error);
            document.getElementById("slate").innerHTML = "Error changing password";
        });
}

// submits the wager predictions
function submitWager() {
    fetch('/getResults', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            predictions: Array.from(document.getElementsByName("bet")).map(el => el.value)
        })
    }).then(function (response) {
        if (response.status !== 200) {
            console.log(response.status);
            throw new Error("Errore nel caricamento delle previsioni");
        }
        return response.text();
    }).then(function (html) {
        document.getElementById("slate").innerHTML = html;
    }).catch(function (error) {
        console.log(error);
        document.getElementById("slate").innerHTML = "Error setting bets";
    })
}

// sets the default rating for the review form
// necessary to represent the rating of a review already submitted
function setDefaultRating(){
    setRating(document.getElementById("rating").value);
}

// assigns prizes to the users
function assignPrizes(){
    fetch('/assignPrizes')
        .then(function (response) {
        if (response.status !== 200) {
            console.log(response.status);
            throw new Error("Errore nell'assegnazione dei premi");
        }
        return response.json();
    }).then(function (json) {
        document.getElementById("assignButton").disabled = true;
        document.getElementById("prize1").innerHTML=json[0];
        document.getElementById("prize2").innerHTML=json[1];
        document.getElementById("prize3").innerHTML=json[2];
    }).catch(function (error) {
        console.log(error);
        document.getElementById("slate").innerHTML = "Error assigning prizes";
    })
}