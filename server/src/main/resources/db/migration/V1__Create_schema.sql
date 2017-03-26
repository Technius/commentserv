create table threads (
       id serial primary key,
       title text not null,
       slug text not null
);

create table comments (
       thread int not null,
       id serial unique not null,
       author int not null,
       content text not null
);
