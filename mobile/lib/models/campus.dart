class Campus {
  final String id;
  final String name;
  final String location;

  Campus({required this.id, required this.name, required this.location});

  factory Campus.fromJson(Map<String, dynamic> json) {
    return Campus(
      id: json['id'] ?? '',
      name: json['name'] ?? '',
      location: json['location'] ?? '',
    );
  }
}
