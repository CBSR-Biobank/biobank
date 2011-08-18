set @pb  = null;

select id from container where product_barcode like ' NU00047136' into @pb;

update container set product_barcode='NU00047136' where id=@pb;
