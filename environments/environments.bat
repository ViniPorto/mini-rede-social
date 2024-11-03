@echo off

::Valores de desenvolvimento, não é de produção
setx MINI_REDE_SOCIAL_DB_HOST "localhost"
setx MINI_REDE_SOCIAL_DB_PORT 1433
setx MINI_REDE_SOCIAL_DB_NAME "mini-rede-social"
setx MINI_REDE_SOCIAL_DB_USER "sa"
setx MINI_REDE_SOCIAL_DB_PASSWORD "12345678"
setx MINI_REDE_SOCIAL_SECURITY_TOKEN_SECRET "12345678"

:: variaives de email
setx MINI_REDE_SOCIAL_EMAIL_HOST "sandbox.smtp.mailtrap.io"
setx MINI_REDE_SOCIAL_EMAIL_PORT 587
setx MINI_REDE_SOCIAL_EMAIL_USERNAME "1c7527f08caab0"
setx MINI_REDE_SOCIAL_EMAIL_PASSWORD "substitua pela senha"
setx MINI_REDE_SOCIAL_EMAIL_ACCOUNT "apiminiredesocial@gmail.com"

pause