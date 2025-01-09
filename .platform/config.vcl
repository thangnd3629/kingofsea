sub vcl_init {
    new lb = directors.round_robin();
    lb.add_backend(server1.backend());
}

sub vcl_recv {
    set req.backend_hint = lb.backend();
}
