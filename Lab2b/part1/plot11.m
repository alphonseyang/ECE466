clc;clear all;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Reading data from a file
%Note that time is in micro seconds and packetsize is in Bytes
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%generator file
[packet_no_p, time_p, packetsize_p] = textread('output.txt', '%f %f %f');

time_array = zeros(1,100000);
cumulative_arrival = zeros(1,100000);

time_array(1) = time_p(1);
cumulative_arrival(1) = packetsize_p(1);
i=2
while i<=100000
    time_array(i) = time_array(i-1) + time_p(i);
    cumulative_arrival(i) = cumulative_arrival(i-1) + packetsize_p(i);
    i=i+1;
end
    
%sink output
[packet_no_p3, packetsize_p3, arrival_time] = textread('TrafficSinkOutput.txt', '%f %f %f');
time_array3 = zeros(1,100000);
cumulative_arrival3 = zeros(1,100000);

time_array3(1) = arrival_time(1);
cumulative_arrival3(1) = packetsize_p3(1);
i=2
while i<=100000
    time_array3(i) = time_array3(i-1) + arrival_time(i);
    cumulative_arrival3(i) = cumulative_arrival3(i-1) + packetsize_p3(i);
    i=i+1;
end

figure(1);

h2 = plot(time_array,cumulative_arrival, 'r', time_array3,cumulative_arrival3, 'b' );
hold on
hkeg2 = legend(h2, 'traffic generator', 'traffic sink');
title('Exercise 1.1');
xlabel('time (in microseconds)');
ylabel('culmulative arrival (in bytes)');