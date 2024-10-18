create table user (id_user serial primary key, nom varchar(32) not null, prenom varchar(32) not null);

create table volontaire (id_volontaire serial primary key, nom varchar(32) not null, prenom varchar(32) not null);

create table admin (id_admin serial primary key, nom varchar(32) not null, prenom varchar(32) not null);

create table demande (id_demande serial primary key, texte varchar(8192) not null, date DATETIME default CURRENT_TIMESTAMP );

create table user_demande (id_user bigint(20) unsigned, id_demande bigint(20) unsigned, statut varchar(32), foreign key (id_user) references user(id_user) on delete cascade, foreign key (id_demande) references demande(id_demande) on delete cascade);

create table user_admin (id_user bigint(20) unsigned, id_admin bigint(20) unsigned, foreign key (id_user) references user(id_user) on delete cascade, foreign key (id_admin) references admin(id_admin) on delete cascade);
