document.addEventListener('DOMContentLoaded', function() {

  var orderPlaced = document.getElementById('orderPlaced')?.value;

    if (orderPlaced === 'true'){
        swal({
            title: "Success!",
            text: "Order placed successfully!",
            icon: "success",
            button: "close!",
        });
    }
});

function showPreview(event){
    if(event.target.files.length > 0){
        var src = URL.createObjectURL(event.target.files[0]);
        var preview = document.getElementById("file-ip-1-preview");
        preview.src = src;
        preview.style.display = "";
    }
}

function cancelOrder(orderId) {
    fetch('/delete-order', {
        method: 'POST',  // Adjust the method as needed
        headers: {
            'Content-Type': 'application/json',
            // Add any other headers if required
        },
        body: JSON.stringify({
            orderId: orderId
        }),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            swal({
                title: "Success!",
                text: "Order Cancel Successfully!",
                icon: "success",
                button: "close!",
            }).then(() => {
              window.location.reload();
            });
        })
        .catch(error => {
            console.error('Error:', error);
            swal({
                title: "Failure!",
                text: "Please try again!",
                icon: "failure",
                button: "close!",
            });
        });
}
