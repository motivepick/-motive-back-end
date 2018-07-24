server {
    listen 443 ssl;
    ssl_certificate /etc/ssl/certs/api-motiv.yaskovdev.com.crt;
    ssl_certificate_key /etc/ssl/certs/api-motiv.yaskovdev.com.key;
    server_name api-motiv.yaskovdev.com;

    location / {
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_pass http://127.0.0.1:8080;
    }
}
