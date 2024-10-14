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
	swal({
		title: 'Confirm Payment',
		text: 'Are you sure you want to make the payment for this billing cycle?',
		icon: 'warning',
		buttons: {
			cancel: true,
			confirm: {
				text: 'Yes, pay now!',
				value: true,
				visible: true,
				className: 'btn btn-primary',
				closeModal: true,
			},
		},
	}).then((willPay) => {
		if (willPay) {
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
						swal('Payment Successful!', 'Your payment has been processed.', 'success').then(() => {
							window.location.reload();
						});
					} else {
						swal('Payment Failed!', 'No unpaid bill found or payment already made.', 'error');
					}
				})
				.catch((error) => {
					swal('Payment Failed!', 'There was an error processing your payment. Please try again.', 'error');
				});
		}
	});
}
