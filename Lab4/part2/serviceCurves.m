figure(1);
x = 1:1:1000;

%y0 = 10000*(x - 20) + 11840;
y1 = 100*x - 8640;
y2 = 500*x - 546480;
y3 = 200*x - 157680;
y4 = 150*x - 12960;
y5 = 175*x - 64800;
y6 = 160*x - 12960;
y7 = 165*x - 15120;


%plot(x,y0,x,y1, x,y2, x, y3, x, y4, x, y5, x, y6);
%plot(x,y0,x,y1, x,y2, x, y3);
plot(x,y1, x,y2, x, y3, x, y4, x, y5, x, y6, x, y7);

title('Estimated Service Curves');
xlabel('Time (milliseconds)');
ylabel('Transmitted Data (bits)');
%legend('Ideal Service Curve', 'experiment 1', 'experiment 2', 'experiment 3');
legend('experiment 1', 'experiment 2', 'experiment 3', 'experiment 4', 'experiment 5', 'experiment 6', 'experiment');
%legend('Ideal Service Curve', 'experiment 1', 'experiment 2', 'experiment 3', 'experiment 4', 'experiment 5', 'experiment 6');