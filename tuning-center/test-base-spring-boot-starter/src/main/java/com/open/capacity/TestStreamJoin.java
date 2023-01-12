package com.open.capacity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.open.capacity.common.join.Join;

public class TestStreamJoin {
	public static void main(String[] args) {
		// 创建两个数据源
		List<Person> persons = Arrays.asList(new Person("Alice", 25), new Person("Bob", 30), new Person("Owen", 30),
				new Person("Charlie", 35));
		List<Dog> dogs = Arrays.asList(new Dog("Alice", "Fido"), new Dog("Bob", "Rufus"), new Dog("Bob", "Tank"),
				new Dog("Charlie", "Spike"));
		// 进行 join 操作
		Stream<BestFriends> bestFriends = Join.leftOuter(persons.stream()).withKey(Person::getName).on(dogs.stream())
				.withKey(Dog::getOwnerName).combine((person, dog) -> new BestFriends(person, dog)).asStream();
		// 将连接的结果转换为一对多形式
		Map<Person, List<Dog>> bestFriendsMap = bestFriends.collect(Collectors.groupingBy(BestFriends::getPerson,
				Collectors.mapping(BestFriends::getDog, Collectors.toList())));
		bestFriendsMap.forEach((person, dogsList) -> System.out.println(person.getName() + " has dogs: " + dogsList));

		System.out.println("===========================");
		// 将连接的结果转换为多对一形式
		Stream<BestFriends> dogsWithPersons = Join.join(dogs.stream()).withKey(Dog::getOwnerName).on(persons.stream())
				.withKey(Person::getName).group((dog, matchingCustomers) -> {
					Person customer = matchingCustomers.findFirst().orElse(null);
					return new BestFriends(customer, dog);
				}).asStream();

		dogsWithPersons.forEach(item -> {
			System.out.println(item.getDog().getName() + " -> " + item.getPerson().getName());
		});

	}

	static class Person {
		String name;
		int age;

		public Person(String name, int age) {
			this.name = name;
			this.age = age;
		}

		public String getName() {
			return name;
		}

		public int getAge() {
			return age;
		}

		@Override
		public String toString() {
			return "Person{" + "name='" + name + '\'' + ", age=" + age + '}';
		}
	}

	static class Dog {
		String ownerName;
		String name;

		public Dog(String ownerName, String name) {
			this.ownerName = ownerName;
			this.name = name;
		}

		public String getOwnerName() {
			return ownerName;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return "Dog{" + "ownerName='" + ownerName + '\'' + ", name='" + name + '\'' + '}';
		}
	}

	static class BestFriends {
		Person person;
		Dog dog;

		public BestFriends(Person person, Dog dog) {
			this.person = person;
			this.dog = dog;
		}

		@Override
		public String toString() {
			return "BestFriends{" + "person=" + person + ", dog=" + dog + '}';
		}

		public Person getPerson() {
			return person;
		}

		public void setPerson(Person person) {
			this.person = person;
		}

		public Dog getDog() {
			return dog;
		}

		public void setDog(Dog dog) {
			this.dog = dog;
		}

	}
}