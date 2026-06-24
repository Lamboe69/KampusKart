import 'package:flutter_test/flutter_test.dart';
import 'package:kampuskart/app.dart';

void main() {
  testWidgets('App loads without errors', (WidgetTester tester) async {
    await tester.pumpWidget(const KampusKartApp());
    await tester.pump();
  });
}
