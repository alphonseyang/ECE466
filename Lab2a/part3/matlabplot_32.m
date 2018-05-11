clc;clear all;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Reading data from a file
%Note that time is in micro seconds and packetsize is in Bytes
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%trace file
[packet_no_p, time_p, packetsize_p] = textread('poisson3.data', '%f %f %f');
time1 = zeros(1,80000);
arrival1 = zeros(1,80000);

time1(1) = time_p(1);
arrival1(1) = packetsize_p(1);
i=2
while i<=80000
    time1(i) = time_p(i);
    arrival1(i) = arrival1(i-1) + packetsize_p(i);
    i=i+1;
end
    
%token bucket output
[arrival_time, packetsize_p2, back_log, num_of_tokens] = textread('bucket_32.txt', '%f %f %f %f');
time2 = zeros(1,80000);
arrival2 = zeros(1,80000);

time2(1) = arrival_time(1);
arrival2(1) = packetsize_p2(1);
i=2
while i<=80000
    time2(i) = time2(i-1) + arrival_time(i);
    arrival2(i) = arrival2(i-1) + packetsize_p2(i);
    i=i+1;
end

%sink output
[packet_no_p3, packetsize_p3, arrival_time] = textread('TrafficSinkOutput_32.txt', '%f %f %f');
time3 = zeros(1,80000);
arrival3 = zeros(1,80000);

time3(1) = arrival_time(1);
arrival3(1) = packetsize_p3(1);
i=2
while i<=80000
    time3(i) = time3(i-1) + arrival_time(i);
    arrival3(i) = arrival3(i-1) + packetsize_p3(i);
    i=i+1;
end

%second plot that shows the content of the token bucket and the backlog
%in the buffer as a function of time
figure(1);
subplot(1,1,1);
num_of_tokens_to_plot = zeros(1,80000);
back_log_to_plot = zeros(1,80000);
i=1;
while i<=80000
    num_of_tokens_to_plot(i) = num_of_tokens(i);
    back_log_to_plot(i) = back_log(i);
    i=i+1;
end
h1 = plot(time2,num_of_tokens_to_plot, 'r',time2, back_log_to_plot,'b')
hold on
hkeg1= legend(h1,'number of tokens','backlog');

title('Token Bucket (bucket_32.txt)');
xlabel('time (in microseconds)');
ylabel('token bucket and backlog');
axis([1 90000 -1000 11000]);


figure(2);
h2 = plot(time1,arrival1, 'r', time2,arrival2, 'g', time3,arrival3, 'b' );
hold on
hkeg2 = legend(h2, 'trace file', 'bucket starter', 'traffic sink');
title('Poisson data');
xlabel('time (in microseconds)');
ylabel('packets arrival (in bytes)');
