/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2017/5/17 21:04:17                           */
/*==============================================================*/


drop table if exists VIP;

drop table if exists VIP_card;

drop table if exists add_money_record;

drop table if exists charge_record;

drop table if exists exchange_record;

drop table if exists level_detail;

drop table if exists manager;

drop table if exists order_item;

drop table if exists order_record;

drop table if exists plan;

drop table if exists prize;

drop table if exists server;

drop table if exists sign_up;

drop table if exists state_change_record;

/*==============================================================*/
/* Table: VIP                                                   */
/*==============================================================*/
create table VIP
(
   client_id            int not null,
   name                 text not null,
   birthday             time,
   phone                int,
   primary key (client_id)
);

/*==============================================================*/
/* Table: VIP_card                                              */
/*==============================================================*/
create table VIP_card
(
   card_id              int not null,
   level_id             int,
   remain_time          int not null,
   point                int not null,
   state                int not null,
   remain_money         int not null,
   primary key (card_id)
);

/*==============================================================*/
/* Table: add_money_record                                      */
/*==============================================================*/
create table add_money_record
(
   card_id              int not null,
   add_time             timestamp not null,
   add_way              text not null,
   add_amount           int not null,
   primary key (card_id, add_time)
);

/*==============================================================*/
/* Table: charge_record                                         */
/*==============================================================*/
create table charge_record
(
   card_id              int not null,
   pay_time             timestamp not null,
   payment              int not null,
   level_id             int,
   add_way              text not null,
   pay_for_time         int not null,
   primary key (card_id, pay_time)
);

/*==============================================================*/
/* Table: exchange_record                                       */
/*==============================================================*/
create table exchange_record
(
   card_id              int not null,
   prize_id             int not null,
   exchange_time        timestamp not null,
   primary key (card_id, prize_id, exchange_time)
);

/*==============================================================*/
/* Table: level_detail                                          */
/*==============================================================*/
create table level_detail
(
   discount             int not null,
   level_id             int not null,
   level_name           text not null,
   money                int not null,
   primary key (level_id)
);

/*==============================================================*/
/* Table: manager                                               */
/*==============================================================*/
create table manager
(
   manager_id           int not null,
   manager_password     text not null,
   primary key (manager_id)
);

/*==============================================================*/
/* Table: order_item                                            */
/*==============================================================*/
create table order_item
(
   item_id              int not null,
   order_id             int,
   plan_id              int,
   seat                 int not null,
   primary key (item_id)
);

/*==============================================================*/
/* Table: order_record                                          */
/*==============================================================*/
create table order_record
(
   buy_time             timestamp not null,
   real_tot_price       int not null,
   buy_way              int not null,
   order_id             int not null,
   card_id              int,
   point_get            int not null,
   primary key (order_id)
);

/*==============================================================*/
/* Table: plan                                                  */
/*==============================================================*/
create table plan
(
   plan_id              int not null,
   server_id            int,
   manager_id           int not null,
   address              text not null,
   start_time           timestamp not null,
   movie_name           text not null,
   room_id              int not null,
   seat_remain          int not null,
   price                int not null,
   point_to_get         int not null,
   spend                float,
   plan_state           int not null,
   primary key (plan_id)
);

/*==============================================================*/
/* Table: prize                                                 */
/*==============================================================*/
create table prize
(
   prize_id             int not null,
   level_id             int,
   prize_name           text not null,
   points_need          int not null,
   primary key (prize_id)
);

/*==============================================================*/
/* Table: server                                                */
/*==============================================================*/
create table server
(
   server_id            int not null,
   server_password      text not null,
   primary key (server_id)
);

/*==============================================================*/
/* Table: sign_up                                               */
/*==============================================================*/
create table sign_up
(
   card_id              int not null,
   client_id            int not null,
   sign_up_time         timestamp not null,
   sign_up_address      text,
   primary key (card_id, client_id)
);

/*==============================================================*/
/* Table: state_change_record                                   */
/*==============================================================*/
create table state_change_record
(
   card_id              int not null,
   modify_time          timestamp not null,
   modify_state         int not null,
   primary key (card_id, modify_time)
);

alter table VIP_card add constraint FK_have foreign key (level_id)
      references level_detail (level_id) on delete restrict on update restrict;

alter table add_money_record add constraint FK_add_money foreign key (card_id)
      references VIP_card (card_id) on delete restrict on update restrict;

alter table charge_record add constraint FK_charge foreign key (card_id)
      references VIP_card (card_id) on delete restrict on update restrict;

alter table charge_record add constraint FK_pay_for_level foreign key (level_id)
      references level_detail (level_id) on delete restrict on update restrict;

alter table exchange_record add constraint FK_card_exchange foreign key (card_id)
      references VIP_card (card_id) on delete restrict on update restrict;

alter table exchange_record add constraint FK_prize_exchange foreign key (prize_id)
      references prize (prize_id) on delete restrict on update restrict;

alter table order_item add constraint FK_Relationship_11 foreign key (order_id)
      references order_record (order_id) on delete restrict on update restrict;

alter table order_item add constraint FK_order_plan foreign key (plan_id)
      references plan (plan_id) on delete restrict on update restrict;

alter table order_record add constraint FK_VIP_buy_ticket foreign key (card_id)
      references VIP_card (card_id) on delete restrict on update restrict;

alter table plan add constraint FK_disicion foreign key (manager_id)
      references manager (manager_id) on delete restrict on update restrict;

alter table plan add constraint FK_make foreign key (server_id)
      references server (server_id) on delete restrict on update restrict;

alter table prize add constraint FK_prize_need_level foreign key (level_id)
      references level_detail (level_id) on delete restrict on update restrict;

alter table sign_up add constraint FK_sign_up foreign key (card_id)
      references VIP_card (card_id) on delete restrict on update restrict;

alter table sign_up add constraint FK_sign_up2 foreign key (client_id)
      references VIP (client_id) on delete restrict on update restrict;

alter table state_change_record add constraint FK_change_state foreign key (card_id)
      references VIP_card (card_id) on delete restrict on update restrict;

