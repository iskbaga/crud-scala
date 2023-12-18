--liquibase formatted sql

--changeset runInTransaction:true failOnError:true
insert into expenses(id, userName, description, amount_integer, amount_fraction, category, location, account, expenseTime)
values (23, 'John Doe', 'Some description', 100, 0, 'Food', 'Home', 'Savings', '10.12.2023'),
(55, 'Jane Smith', 'Dinner out', 75, 33, 'Dining', 'Restaurant A', 'Checking', '11.12.2023'),
(17, 'John Doe', 'Groceries', 50, 25, 'Food', 'Local Supermarket', 'Credit Card', '17.12.2022'),
(29, 'Ivan Ivanov', 'Dinner', 30, 75, 'Dining', 'Restaurant', 'Cash', '06.12.2022'),
(31, 'John Ivanov', 'Gasoline', 40, 50, 'Transportation', 'Gas Station', 'Credit Card', '19.11.2022'),
(48, 'Sergey', 'Electronics', 120, 0, 'Shopping', 'Tech Store', 'Online Wallet', '06.07.2022'),
(51, 'Ruslan', 'Movie Night', 20, 50, 'Entertainment', 'Cinema', 'Cash', '06.07.2022'),
(52, 'Danil', 'Movie Night', 20, 50, 'Entertainment', 'Cinema', 'Cash', '06.07.2022'),
(4, 'Bob Johnson', 'Gasoline', 40,  21, 'Transportation', 'Gas Station', 'Credit Card', '12.12.2023');
--rollback DELETE FROM expenses
