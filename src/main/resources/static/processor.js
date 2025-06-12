function setActiveTab(tab){
    document.getElementById(tab).setAttribute("class", "nav-link active")
}
function setActiveSportTab(tab){
    document.getElementById(tab).setAttribute("class", "nav-link active")
    document.getElementById("sport-tab-list").setAttribute("class", "nav-link active dropdown-toggle")
}

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
        })
        .catch(function(error){
            console.log(error);
            document.getElementById("slate").innerHTML = "Error loading dashboard";
        })
}

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

function showMessage() {
    document.getElementById("message").style.display = "block";
}

function hideMessage() {
    document.getElementById("message").style.display = "none";
}

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

function validatePasswordLogin() {
    document.getElementById("submit-btn").disabled = !validatePassword();
}

function calculate_age(dob) {
    // Calculate the difference in milliseconds between the current date and the provided date of birth
    var diff_ms = Date.now() - dob.getTime();
    // Create a new Date object representing the difference in milliseconds and store it in the variable age_dt (age Date object)
    var age_dt = new Date(diff_ms);

    // Calculate the absolute value of the difference in years between the age Date object and the year 1970 (UNIX epoch)
    return Math.abs(age_dt.getUTCFullYear() - 1970);
}

function isValidDateFormat(dateStr) {
    // Regular expression to match the format dd/MM/yyyy
    const dateRegex = /^(0[1-9]|[12][0-9]|3[01])\/(0[1-9]|1[0-2])\/\d{4}$/;

    // Test if the date string matches the regex
    if (!dateRegex.test(dateStr)) {
        return false; // Invalid format
    }

    // Split the date string into day, month, year
    const [day, month, year] = dateStr.split('/').map(Number);

    // Check the validity of the date using the JavaScript Date object
    const date = new Date(year, month - 1, day); // Months are 0-based in JS
    return date.getFullYear() === year &&
        date.getMonth() === month - 1 &&
        date.getDate() === day;
}


function validateCredentials(){
    var passwordCheck = validatePassword();
    var matchCheck = matchPassword();
    var dateFormatCheck = isValidDateFormat(document.getElementById("birthdate").value);
    console.log(document.getElementById("birthdate").value)
    var ageCheck = calculate_age(new Date(document.getElementById("birthdate").value)) >= 18;

    if(!ageCheck && document.getElementById("birthdate").value !== ""){
        document.getElementById("ageAlert").style.display = "block";
    }else{
        document.getElementById("ageAlert").style.display = "none";
    }

    if (passwordCheck && matchCheck && //dateFormatCheck &&
        ageCheck) {
        document.getElementById("submit-btn").disabled = false;
    } else{
        document.getElementById("submit-btn").disabled = true;
    }
}

function validatePasswordChange(){
    var passwordCheck = validatePassword();
    var matchCheck = matchPassword();
    if (matchCheck && passwordCheck){
        document.getElementById("submit-btn").disabled = false;
    } else {
        document.getElementById("submit-btn").disabled = true;
    }
}

function resetForm(){
    document.getElementById("submit-btn").disabled = true;
    document.getElementById("messageMatch").style.display = "none";
}

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

function getNews(){
    fetch(`/news`)
        .then(function (response){
            if (response.status !== 200) {
                console.log(response.status);
                throw new Error("Errore nel loading del file JSON");}
            console.log("a")
            return response.json();
        })
        .then(function (json) {
            const container = document.getElementById("newsList");
            container.innerHTML = '';
            console.log("a");
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
                console.log("b")
            })
        })
        .catch(function(error){
            console.log(error);
            document.getElementById("newsList").innerHTML = "Error loading news";
        })
}

function promote(userId) {
    fetch(`/promote`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'userId': userId
        }
    })
        .then(function (response) {
            if (response.status !== 200) {
                console.log(response.status);
                throw new Error("Errore nel caricamento della promozione");
            }
        })
        .then(function (html) {
            document.getElementById("slate").innerHTML = html;
        })
        .catch(function (error) {
            console.log(error);
            document.getElementById("slate").innerHTML = "Error promoting user";
        });
}