package com.github.mproberts.rxdatabindingdemo.di;

import android.content.Context;

import com.github.mproberts.rxdatabindingdemo.data.UserId;
import com.github.mproberts.rxdatabindingdemo.data.UserStorage;
import com.github.mproberts.rxdatabindingdemo.storage.RoomStorage;
import com.github.mproberts.rxdatabindingdemo.storage.RoomUser;

import java.util.Arrays;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

@Module
public class RoomModule {

    public RoomModule() {
    }

    @Provides
    @Singleton
    RoomStorage providesRoomStorage(Context context) {
        Scheduler scheduler = Schedulers.single();
        RoomStorage inMemory = RoomStorage.createInMemory(context, scheduler);


        scheduler.scheduleDirect(() -> {
            inMemory.userDao().updateUsers(Arrays.asList(
                    new RoomUser(new UserId("1"), "Robert Downey", "https://images-na.ssl-images-amazon.com/images/M/MV5BNzg1MTUyNDYxOF5BMl5BanBnXkFtZTgwNTQ4MTE2MjE@._V1_UX256_CR0,0,256,256_AL_.jpg", "Iron_Man", true),
                    new RoomUser(new UserId("2"), "Chris Hemsworth", "https://images-na.ssl-images-amazon.com/images/M/MV5BOTU2MTI0NTIyNV5BMl5BanBnXkFtZTcwMTA4Nzc3OA@@._V1_UX256_CR0,0,256,256_AL_.jpg", "Thor", true),
                    new RoomUser(new UserId("3"), "Mark Ruffalo", "https://images-na.ssl-images-amazon.com/images/M/MV5BNDQyNzMzZTMtYjlkNS00YzFhLWFhMTctY2M4YmQ1NmRhODBkXkEyXkFqcGdeQXVyNjcyNzgyOTE@._V1_UY44_CR1,0,32,44_AL_.jpg", "Hulk", true),
                    new RoomUser(new UserId("4"), "Chris Evans", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTU2NTg1OTQzMF5BMl5BanBnXkFtZTcwNjIyMjkyMg@@._V1_UX256_CR0,0,256,256_AL_.jpg", "Captain_America", true),
                    new RoomUser(new UserId("5"), "Scarlett Johansson", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTM3OTUwMDYwNl5BMl5BanBnXkFtZTcwNTUyNzc3Nw@@._V1_UY44_CR2,0,32,44_AL_.jpg", "Black_Widow", true),
                    new RoomUser(new UserId("6"), "Jeremy Renner", "https://images-na.ssl-images-amazon.com/images/M/MV5BOTk2NDc2ODgzMF5BMl5BanBnXkFtZTcwMTMzOTQ4Nw@@._V1_UX256_CR0,0,256,256_AL_.jpg", "Hawkeye", true),
                    new RoomUser(new UserId("7"), "James Spader", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTQ3MTQ5NjY5Ml5BMl5BanBnXkFtZTgwMTY0NzU5MDE@._V1_UY44_CR0,0,32,44_AL_.jpg", "Ultron", false),
                    new RoomUser(new UserId("8"), "Samuel L", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTQ1NTQwMTYxNl5BMl5BanBnXkFtZTYwMjA1MzY1._V1_UX256_CR0,0,256,256_AL_.jpg", "Nick_Fury", true),
                    new RoomUser(new UserId("9"), "Don Cheadle", "https://images-na.ssl-images-amazon.com/images/M/MV5BNDMxNDM3MzY5N15BMl5BanBnXkFtZTcwMjkzOTY4MQ@@._V1_UY44_CR1,0,32,44_AL_.jpg", "War_Machine", true),
                    new RoomUser(new UserId("10"), "Aaron Taylor", "https://images-na.ssl-images-amazon.com/images/M/MV5BMzE5MzI0MzY2OF5BMl5BanBnXkFtZTgwODE3MTk4MTE@._V1_UY44_CR1,0,32,44_AL_.jpg", "Quicksilver", true),
                    new RoomUser(new UserId("11"), "Elizabeth Olsen", "https://images-na.ssl-images-amazon.com/images/M/MV5BMjEzMjA0ODk1OF5BMl5BanBnXkFtZTcwMTA4ODM3OQ@@._V1_UX256_CR0,0,256,256_AL_.jpg", "Scarlet_Witch", true),
                    new RoomUser(new UserId("12"), "Paul Bettany", "https://images-na.ssl-images-amazon.com/images/M/MV5BMjEwODg1MTA5Ml5BMl5BanBnXkFtZTcwNDQwMTQxMw@@._V1_UY44_CR0,0,32,44_AL_.jpg", "Vision", true),
                    new RoomUser(new UserId("13"), "Cobie Smulders", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTkzNTUyMTczM15BMl5BanBnXkFtZTcwMjMxNTM4Nw@@._V1_UX256_CR0,0,256,256_AL_.jpg", "Maria_Hill", false),
                    new RoomUser(new UserId("14"), "Anthony Mackie", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTk3NTM1MjE2M15BMl5BanBnXkFtZTcwNzc5OTI2Mg@@._V1_UY44_CR1,0,32,44_AL_.jpg", "Falcon", true),
                    new RoomUser(new UserId("15"), "Hayley Atwell", "https://images-na.ssl-images-amazon.com/images/M/MV5BMmFiNWZlZDktMTY0NS00MDJjLWIxYzUtYzRhOTc2ZTQxYzNiXkEyXkFqcGdeQXVyNzM0MjE0NDk@._V1_UY44_CR6,0,32,44_AL_.jpg", "Peggy_Carter", false),
                    new RoomUser(new UserId("16"), "Idris Elba", "https://images-na.ssl-images-amazon.com/images/M/MV5BNzEzMTI2NjEyNF5BMl5BanBnXkFtZTcwNTA0OTE4OA@@._V1_UX256_CR0,0,256,256_AL_.jpg", "Heimdall", false),
                    new RoomUser(new UserId("17"), "Linda Cardellini", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTQ2MDM4MTM2NF5BMl5BanBnXkFtZTgwMTM4MjYyMDE@._V1_UX256_CR0,0,256,256_AL_.jpg", "Laura_Barton", false),
                    new RoomUser(new UserId("18"), "Stellan Skarsgård", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTg4NDUzOTY0N15BMl5BanBnXkFtZTYwNjYxODE0._V1_UX256_CR0,0,256,256_AL_.jpg", "Erik_Selvig", false),
                    new RoomUser(new UserId("19"), "Claudia Kim", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTcyMzgxNTM4N15BMl5BanBnXkFtZTgwMjY1ODk4MzE@._V1_UX256_CR0,0,256,256_AL_.jpg", "Dr._Helen_Cho", false),
                    new RoomUser(new UserId("20"), "Thomas Kretschmann", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTY1Njc5MzE1OF5BMl5BanBnXkFtZTcwMTc1NDM4Nw@@._V1_UX256_CR0,0,256,256_AL_.jpg", "Strucker", false),
                    new RoomUser(new UserId("21"), "Andy Serkis", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTIwNzI2OTA3Nl5BMl5BanBnXkFtZTYwNDIwNzA1._V1_UX256_CR0,0,256,256_AL_.jpg", "Ulysses_Klaue", false),
                    new RoomUser(new UserId("22"), "Julie Delpy", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTg4ODM0MjI5NV5BMl5BanBnXkFtZTYwNDQ5NjM1._V1_UY44_CR0,0,32,44_AL_.jpg", "Madame_B", false),
                    new RoomUser(new UserId("23"), "Stan Lee", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTk3NDE3Njc5M15BMl5BanBnXkFtZTYwOTY5Nzc1._V1_UX256_CR0,0,256,256_AL_.jpg", "Stan_Lee", false),
                    new RoomUser(new UserId("24"), "Henry Goodman", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTU1MjQ2MTgyNl5BMl5BanBnXkFtZTcwOTA1MDAxOA@@._V1_UX256_CR0,0,256,256_AL_.jpg", "Dr._List", false),
                    new RoomUser(new UserId("25"), "Chris Luca", "https://images-na.ssl-images-amazon.com/images/M/MV5BZGVjYTJjOTQtZmU2MC00NmYyLWEzYjctNTU5MGEyNDQ2MmVjXkEyXkFqcGdeQXVyMzc4NjExMQ@)@._V1_UX256_CR0,0,256,256_AL_.jpg", "Fortress_Soldier", false),
                    new RoomUser(new UserId("26"), "Brian Schaeffer", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTk2OTA3NzE4MF5BMl5BanBnXkFtZTcwNDg4NjQ0Mw@@._V1_UX256_CR0,0,256,256_AL_.jpg", "Strucker_Mercenary", false),
                    new RoomUser(new UserId("27"), "Dominique Provost", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTk5Mjg2MDc0MV5BMl5BanBnXkFtZTgwOTE2NDY3NjE@._V1_UY44_CR6,0,32,44_AL_.jpg", "Zrinka", false),
                    new RoomUser(new UserId("28"), "Isaac Andrews", "https://images-na.ssl-images-amazon.com/images/M/MV5BMzU4NWIxY2MtOTk4MC00OGIxLTlkY2UtZjBlZjUyODZiMmQ5L2ltYWdlL2ltYWdlXkEyXkFqcGdeQXVyNTc3MjUzNTI@._V1_UY44_CR23,0,32,44_AL_.jpg", "Costel", false),
                    new RoomUser(new UserId("29"), "Gareth Kieran", null, "Sokovian_Acid_Student", false),
                    new RoomUser(new UserId("30"), "Chan Woo", null, "Dr._Cho's_Assistant", false),
                    new RoomUser(new UserId("31"), "Minhee Yeo", "https://images-na.ssl-images-amazon.com/images/M/MV5BOTkzOTM4ODA5OV5BMl5BanBnXkFtZTgwOTYyMzYwODE@._V1_UX256_CR0,0,256,256_AL_.jpg", "Dr._Cho's_Assistant", false),
                    new RoomUser(new UserId("32"), "Bentley Kalu", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTcxODY4NjM1MF5BMl5BanBnXkFtZTgwODY3NjQxMjE@._V1_UY44_CR1,0,32,44_AL_.jpg", "Klaue's_Mercenary", false),
                    new RoomUser(new UserId("33"), "Julian Bleach", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTg4ODcxODM1Nl5BMl5BanBnXkFtZTgwODE2MzAyNjE@._V1_UX256_CR0,0,256,256_AL_.jpg", "Ballet_Instructor", false),
                    new RoomUser(new UserId("34"), "Christopher Beasley", null, "Johannesburg_Cop", false),
                    new RoomUser(new UserId("35"), "Vuyo Dabula", null, "Johannesburg_Cop", false),
                    new RoomUser(new UserId("36"), "Nondumiso Tembe", "https://images-na.ssl-images-amazon.com/images/M/MV5BZThiNDMwNmUtYTAzMi00MmRhLTk2YTAtMDE4NzNmNTBkMWM2XkEyXkFqcGdeQXVyMjEwMjE2Mjc@._V1_UY44_CR0,0,32,44_AL_.jpg", "Johannesburg_Driver", false),
                    new RoomUser(new UserId("37"), "Kabelo Thai", null, "Johannesburg_Onlooker", false),
                    new RoomUser(new UserId("38"), "Lele Ledwaba", null, "Johannesburg_Onlooker", false),
                    new RoomUser(new UserId("39"), "Mandla Gaduka", null, "Johannesburg_Onlooker", false),
                    new RoomUser(new UserId("40"), "Harriet Manamela", null, "Johannesburg_Onlooker", false),
                    new RoomUser(new UserId("41"), "Beulah Hashe", null, "Johannesburg_Onlooker", false),
                    new RoomUser(new UserId("42"), "Musca Kumalo", null, "Johannesburg_Onlooker", false),
                    new RoomUser(new UserId("43"), "Mathapelo September", null, "Johannesburg_Onlooker", false),
                    new RoomUser(new UserId("44"), "Antony Acheampong", "https://images-na.ssl-images-amazon.com/images/M/MV5BOWU0MTI5YjgtM2YzYS00YWIzLWE3YmYtMzFiOGMxYzk1MWE4XkEyXkFqcGdeQXVyNTM5MjcyNTQ@._V1_UY44_CR1,0,32,44_AL_.jpg", "Johannesburg_Elevator_Passenger", false),
                    new RoomUser(new UserId("45"), "Chioma Anyanwu", null, "Johannesburg_Elevator_Passenger", false),
                    new RoomUser(new UserId("46"), "Ben Sakamoto", "https://images-na.ssl-images-amazon.com/images/M/MV5BOWEwYzZlZjMtZGM2MC00OTZhLTkxMjYtN2ZkNWY4YzgwODI5XkEyXkFqcGdeQXVyMjQwMDg0Ng@)@._V1_UY44_CR12,0,32,44_AL_.jpg", "Cooper_Barton", false),
                    new RoomUser(new UserId("47"), "Imogen Poynton", null, "Lila_Barton", false),
                    new RoomUser(new UserId("48"), "Isabella Poynton", null, "Lila_Barton", false),
                    new RoomUser(new UserId("49"), "Ingvild Deila", "https://images-na.ssl-images-amazon.com/images/M/MV5BZGI5NmYyODQtODg0OS00NjI3LWEzZWItNTA2NGM3MDU1YjUzXkEyXkFqcGdeQXVyMjU5MjIyMTE@._V1_UY44_CR9,0,32,44_AL_.jpg", "World_Hub_Tech", false),
                    new RoomUser(new UserId("50"), "Sunny Yeo", "https://images-na.ssl-images-amazon.com/images/M/MV5BZDIzNGU5OGMtOTJmZS00N2ZkLTk2ZDQtZWU4NjdmOWY4ZWY0L2ltYWdlXkEyXkFqcGdeQXVyNTA1OTA1OTU@._V1_UX256_CR0,0,256,256_AL_.jpg", "Korean_Train_Passenger", false),
                    new RoomUser(new UserId("51"), "Namju Go", null, "Korean_Train_Passenger", false),
                    new RoomUser(new UserId("52"), "Mina Kweon", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTc3NzM4MzQwNl5BMl5BanBnXkFtZTgwNzIxMTQ1NTE@._V1_UY44_CR1,0,32,44_AL_.jpg", "Korean_Train_Passenger", false),
                    new RoomUser(new UserId("53"), "Earl T", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTQ1ODY5ODc1Nl5BMl5BanBnXkFtZTgwODU2MjM5MzE@._V1_UX256_CR0,0,256,256_AL_.jpg", "Korean_Train_Passenger", false),
                    new RoomUser(new UserId("54"), "Arthur Lee", null, "Korean_Train_Passenger", false),
                    new RoomUser(new UserId("55"), "Verity Hewlitt", null, "Sokovian_Family", false),
                    new RoomUser(new UserId("56"), "Michael Matovski", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTUxNTYxNjg1Ml5BMl5BanBnXkFtZTgwMjA4NjE1MDI@._V1_UY44_CR7,0,32,44_AL_.jpg", "Sokovian_Family", false),
                    new RoomUser(new UserId("57"), "Alma Noce", null, "Sokovian_Family", false),
                    new RoomUser(new UserId("58"), "Riccardo Richetta", null, "Sokovian_Family", false),
                    new RoomUser(new UserId("59"), "Constanza Ruff", null, "Sokovian_Woman", false),
                    new RoomUser(new UserId("60"), "Monty Mclaren", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTQ2MTg2Njc2N15BMl5BanBnXkFtZTgwMjQ4ODI5MjE@._V1_UY44_CR2,0,32,44_AL_.jpg", "Tub_Family_Child", false),
                    new RoomUser(new UserId("61"), "Julia Krynke", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTM5MDYwMTc5Ml5BMl5BanBnXkFtZTcwOTI4NDc3NQ@@._V1_UY44_CR17,0,32,44_AL_.jpg", "Sokovian_Driver", false),
                    new RoomUser(new UserId("62"), "Tony Christian", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTU0Nzc0NjczMl5BMl5BanBnXkFtZTcwNTE2NDcxOA@@._V1_UY44_CR11,0,32,44_AL_.jpg", "Sokovian_SUV_Driver", false),
                    new RoomUser(new UserId("63"), "Ian Kay", "https://images-na.ssl-images-amazon.com/images/M/MV5BMjAxMDE1MDU3NF5BMl5BanBnXkFtZTgwNjM2MjAyNjE@._V1_UY44_CR2,0,32,44_AL_.jpg", "Sokovian_SUV_Passenger", false),
                    new RoomUser(new UserId("64"), "Barry Aird", "https://images-na.ssl-images-amazon.com/images/M/MV5BOTEzMzUyMTQwN15BMl5BanBnXkFtZTgwOTcyNjQ1ODE@._V1_UY44_CR1,0,32,44_AL_.jpg", "Sokovian_Police_Captain", false),
                    new RoomUser(new UserId("65"), "Aaron Himelstein", "https://images-na.ssl-images-amazon.com/images/M/MV5BNDA2NDg2NDUyMV5BMl5BanBnXkFtZTgwMjc1NzQ2NTE@._V1_UY44_CR1,0,32,44_AL_.jpg", "Specialist_Cameron_Klein", false),
                    new RoomUser(new UserId("66"), "Kerry Condon", "https://images-na.ssl-images-amazon.com/images/M/MV5BMTY5NjgyNDU3OV5BMl5BanBnXkFtZTgwNzA4NDczNjE@._V1_UY44_CR1,0,32,44_AL_.jpg", "Friday", false),
                    new RoomUser(new UserId("67"), "Jaiden Stafford", null, "Nathaniel_Pietro_Barton", false),
                    new RoomUser(new UserId("68"), "Joseph M", null, "Party_Guest", false),
                    new RoomUser(new UserId("69"), "Hassan Ahmed", null, "Somali_Shipworker", false),
                    new RoomUser(new UserId("70"), "Mohammed Ali", null, "Worker_in_a_Scientific_Laboratory", false),
                    new RoomUser(new UserId("71"), "Pavlina Andreevska", null, "Sokovian", false),
                    new RoomUser(new UserId("72"), "Freddie Andrews", null, "Party_Guest", false),
                    new RoomUser(new UserId("73"), "David Olawale", null, "Somalian_Ship_Worker", false),
                    new RoomUser(new UserId("74"), "Nikita Baron", null, "Ballet_Dancer", false),
                    new RoomUser(new UserId("75"), "Gintare Beinoraviciute", null, "Citizen", false),
                    new RoomUser(new UserId("76"), "Francesca Bennett", null, "Sokovian_Driver", false),
                    new RoomUser(new UserId("77"), "Laura Bernardeschi", null, "Sokovian_Citizen", false),
                    new RoomUser(new UserId("78"), "Dilyana Bouklieva", null, "Sokovian_Citizen", false),
                    new RoomUser(new UserId("79"), "Dante Briggins", null, "Party_Guest", false),
                    new RoomUser(new UserId("80"), "Josh Brolin", null, "Thanos", false),
                    new RoomUser(new UserId("81"), "Rowdy Brown", null, "Cop", false),
                    new RoomUser(new UserId("82"), "Billy Burke", null, "Dancer", false),
                    new RoomUser(new UserId("83"), "Cheryl Burniston", null, "Sergeant", false),
                    new RoomUser(new UserId("84"), "Abbey Marise", null, "American", false),
                    new RoomUser(new UserId("85"), "Michael Chapman", null, "Lab_Tech_Scientist", false),
                    new RoomUser(new UserId("86"), "Tino Chinyoka", null, "Somalian_Ship_Commander", false),
                    new RoomUser(new UserId("87"), "Leigh Daniels", null, "Dancer", false),
                    new RoomUser(new UserId("88"), "Marianna Dean", null, "Party_Guest", false),
                    new RoomUser(new UserId("89"), "Lukas DiSparrow", null, "Sok", false),
                    new RoomUser(new UserId("90"), "Thom Dobbin", null, "Party_Guest", false),
                    new RoomUser(new UserId("91"), "Alexandra Doyle", null, "Reporter", false),
                    new RoomUser(new UserId("92"), "RR.P", null, "College_Student", false),
                    new RoomUser(new UserId("93"), "Keith Fausnaught", null, "Pedestrian", false),
                    new RoomUser(new UserId("94"), "Aurora Fearnley", null, "Marine", false),
                    new RoomUser(new UserId("95"), "Lou Ferrigno", null, "Hulk", false),
                    new RoomUser(new UserId("96"), "Hannah Flynn", null, "Dancer", false),
                    new RoomUser(new UserId("97"), "Robert J", null, "Youth", false),
                    new RoomUser(new UserId("98"), "Alex Gillison", null, "Masked_Asgardian", false),
                    new RoomUser(new UserId("99"), "Sophie Gooding", null, "Club_Dancer", false),
                    new RoomUser(new UserId("100"), "Guna Gultniece", null, "Student", false),
                    new RoomUser(new UserId("101"), "Mark Haldor", null, "Asgardian", false),
                    new RoomUser(new UserId("102"), "Beshoy Hanna", null, "Pedestrian", false),
                    new RoomUser(new UserId("103"), "Salem Hanna", null, "Party_Guest", false),
                    new RoomUser(new UserId("104"), "Sam Hanover", null, "Sokovian_Citizen", false),
                    new RoomUser(new UserId("105"), "Anthony Henry", null, "Charlie_Nash", false),
                    new RoomUser(new UserId("106"), "Jason Her", null, "TV_News_Anchor", false),
                    new RoomUser(new UserId("107"), "Leigh Holland", null, "Masked_Asgardian", false),
                    new RoomUser(new UserId("108"), "Kornelia Horvath", null, "Sokovian", false),
                    new RoomUser(new UserId("109"), "Mohamed Mozii", null, "Somali_Ship_Commander", false),
                    new RoomUser(new UserId("110"), "Bron James", null, "Lab_Technician", false),
                    new RoomUser(new UserId("111"), "Mariola Jaworska", null, "Sokovian", false),
                    new RoomUser(new UserId("112"), "Marcus G", null, "Shield_Agent", false),
                    new RoomUser(new UserId("113"), "Minouche Kaftel", null, "Slovakian", false),
                    new RoomUser(new UserId("114"), "Attila G", null, "Sokovian_Citizen", false),
                    new RoomUser(new UserId("115"), "Denis Khoroshko", null, "Sokovian_Driver_Pavlov", false),
                    new RoomUser(new UserId("116"), "Hrvoje Klecz", null, "Party_Guest", false),
                    new RoomUser(new UserId("117"), "Adrian Klein", null, "Sokovian_Policeman", false),
                    new RoomUser(new UserId("118"), "Irina Klimovich", null, "Sokovian_Students", false),
                    new RoomUser(new UserId("119"), "Kai Kyriacou", null, "Child_1", false),
                    new RoomUser(new UserId("120"), "Lex Lang", null, "Sokovian_Soldier", false),
                    new RoomUser(new UserId("121"), "Jamie Lengyel", null, "Evil_Hydra_Scientist", false),
                    new RoomUser(new UserId("122"), "Jorge Leon", null, "Stark_Guest", false),
                    new RoomUser(new UserId("123"), "Marian Lorencik", null, "Sokovian", false),
                    new RoomUser(new UserId("124"), "Edina Loskay", null, "Sokovian", false),
                    new RoomUser(new UserId("125"), "Bartosz Malarski", null, "Sokovian_Student", false),
                    new RoomUser(new UserId("126"), "Lena Milan", null, "Sokovian_Agent", false),
                    new RoomUser(new UserId("127"), "Eric Morcos", null, "The_Agent", false),
                    new RoomUser(new UserId("128"), "Joti Nagra", null, "Student", false),
                    new RoomUser(new UserId("129"), "Nick W", null, "Dr._Taryl_Jenkins", false),
                    new RoomUser(new UserId("130"), "Judit Novotnik", null, "Sokovian", false),
                    new RoomUser(new UserId("131"), "Emeson Nwolie", null, "Businessman", false),
                    new RoomUser(new UserId("132"), "Sigmund Oakeshott", null, "USO_Military_Policeman", false),
                    new RoomUser(new UserId("133"), "Andrea-Andrea-Nichole", null, "S.H.I.E.L.D_agent", false),
                    new RoomUser(new UserId("134"), "Zakk Pierce", null, "Sokovian_Boy", false),
                    new RoomUser(new UserId("135"), "Andrew James", null, "Party_Guest", false),
                    new RoomUser(new UserId("136"), "Guy Potter", null, "British_Officer", false),
                    new RoomUser(new UserId("137"), "Tim Powell", null, "DEA_Director", false),
                    new RoomUser(new UserId("138"), "Diezel Ramos", null, "Masked_Asgardian", false),
                    new RoomUser(new UserId("139"), "Dagny Rollins", null, "Girl_on_the_Train", false),
                    new RoomUser(new UserId("140"), "Anthony J", null, "Party_Guest", false),
                    new RoomUser(new UserId("141"), "Danielle Saunders", null, "S.H.I.E.L.D._Agent", false),
                    new RoomUser(new UserId("142"), "Faye Sewell", null, "Oslo_Hub_Scientist", false),
                    new RoomUser(new UserId("143"), "Megyn Shott", null, "Car_Driver", false),
                    new RoomUser(new UserId("144"), "Bari Suzuki", null, "Shield_Agent", false),
                    new RoomUser(new UserId("145"), "Georgie-Georgie-May", null, "Student", false),
                    new RoomUser(new UserId("146"), "Chris Townsend", null, "Party_Guest", false),
                    new RoomUser(new UserId("147"), "Bartosz Wandrykow", null, "Sokovian_Policeman", false),
                    new RoomUser(new UserId("148"), "Daniel Westwood", null, "Asgardian_God", false),
                    new RoomUser(new UserId("149"), "Ben Wombwell", null, "Party_Guest", false),
                    new RoomUser(new UserId("150"), "Tatiana Zarubova", null, "Sokovian", false)
            ));
        });

        return inMemory;
    }

    @Provides
    UserStorage providesUsers(RoomStorage roomDb) {
        return roomDb.users();
    }
}
