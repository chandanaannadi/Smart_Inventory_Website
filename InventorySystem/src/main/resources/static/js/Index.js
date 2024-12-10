document.addEventListener('DOMContentLoaded', function () {
	var orderPlaced = document.getElementById('orderPlaced')?.value;

	if (orderPlaced === 'true') {
		swal({
			title: 'Success!',
			text: 'Order placed successfully!',
			icon: 'success',
			button: 'close!',
		});
	}

	checkUnreadMessages();
});

function showPreview(event) {
	if (event.target.files.length > 0) {
		var src = URL.createObjectURL(event.target.files[0]);
		var preview = document.getElementById('file-ip-1-preview');
		preview.src = src;
		preview.style.display = '';
	}
}

function cancelOrder(orderId) {
	fetch('/delete-order', {
		method: 'POST', // Adjust the method as needed
		headers: {
			'Content-Type': 'application/json',
			// Add any other headers if required
		},
		body: JSON.stringify({
			orderId: orderId,
		}),
	})
		.then((response) => {
			if (!response.ok) {
				throw new Error('Network response was not ok');
			}
			swal({
				title: 'Success!',
				text: 'Order Cancel Successfully!',
				icon: 'success',
				button: 'close!',
			}).then(() => {
				window.location.reload();
			});
		})
		.catch((error) => {
			console.error('Error:', error);
			swal({
				title: 'Failure!',
				text: 'Please try again!',
				icon: 'failure',
				button: 'close!',
			});
		});
}

function makePayment(billId) {
    // Directly call the API without the confirmation dialog
    fetch('/billing/pay', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ billId: billId }),
    })
    .then((response) => {
        if (response.ok) {
            return response.text();
        } else {
            throw new Error('Network response was not ok.');
        }
    })
    .then((success) => {
        if (success === 'true') {
            // Optionally show a message or reload the page
            alert('Payment Successful! Your payment has been processed.');
           // window.location.reload();
        } else {
            alert('Payment Failed! No unpaid bill found or payment already made.');
        }
    })
    .catch((error) => {
        alert('Payment Failed! There was an error processing your payment. Please try again.');
    });
}

async function checkUnreadMessages() {
	const response = await fetch('/messages/unread-status');
	if (!response.ok) {
		console.error(`Error fetching unread message status: ${response.statusText}`);
		return;
	}
	const hasUnreadMessages = await response.json();

	const chatStatusIcon = document.getElementById('chatStatusIcon');
	if (hasUnreadMessages) {
		chatStatusIcon.style.color = 'green';
	} else {
		chatStatusIcon.style.color = 'grey';
	}
}
