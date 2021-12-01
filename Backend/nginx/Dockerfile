FROM nginx:1.21.4

RUN  rm -rf /etc/nginx/conf.d/default.conf

COPY conf.d/proxy.conf /etc/nginx/conf.d/proxy.conf

EXPOSE 8080

CMD ["nginx", "-g", "daemon off;"]