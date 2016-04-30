gcc deploy_wrapper.c -o deploy_wrapper
chown root deploy_wrapper
chmod u=rwx,go=xr,+s deploy_wrapper
