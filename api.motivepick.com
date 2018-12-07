server {
    listen 443 ssl;
    ssl_certificate /etc/letsencrypt/live/api.motivepick.com/fullchain.pem; # managed by Certbot
    ssl_certificate_key /etc/letsencrypt/live/api.motivepick.com/privkey.pem; # managed by Certbot

    server_name api.motivepick.com;

    location / {
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_pass http://127.0.0.1:8080;
    }
}
