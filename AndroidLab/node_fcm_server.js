var admin=require('firebase-admin')
var serviceAccount=require('./server_key.json')

admin.initializeApp({
    credential:admin.credential.cert(serviceAccount)
})

var token="cCuRdupMQPSjvFgXVdfNZf:APA91bFeaV2PZ7Z67yfU_j5jr4FLX6ZYRSLTltqvrTcthIAwhNgnMQAzRvfx8AfnKlsFJESt5DABiHu-L8excLQfC601WZ8zC8FfWylevOQrE8ioftM-zdCrS1K7qMYMPcN2NiCUm54K"

var fcm_message={
    notification:{
        title:'noti title',
        body: 'noti body..'
    },
    data:{
        title:'data title',
        value: '20'
    },
    token:token
}

admin.messaging().send(fcm_message)
.then(function(response){
    console.log('send ok...')
})
.catch(function(error){
    console.log('send error...')
})