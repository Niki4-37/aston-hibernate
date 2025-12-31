FROM alpine:3

RUN apk add --no-cache curl jq

COPY register_services.sh /register_services.sh

CMD ["/register_services.sh"]